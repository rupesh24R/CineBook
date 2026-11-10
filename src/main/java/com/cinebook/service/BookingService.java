package com.cinebook.service;

import com.cinebook.model.Booking;
import com.cinebook.model.Seat;
import com.cinebook.model.Show;
import com.cinebook.util.BookingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service orchestrating booking transactions, cancellations, pricing, and business rules.
 * Demonstrates:
 * - Service layer pattern separating business logic from UI and data models.
 * - Atomic operations ensuring data consistency.
 * - Exception handling for validation and state errors.
 */
public class BookingService {
    private final Theatre theatre;

    /**
     * Constructs the BookingService associated with a Theatre instance.
     *
     * @param theatre the cinema theatre repository
     */
    public BookingService(Theatre theatre) {
        this.theatre = theatre;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    /**
     * Validates a batch of requested seat codes against a show before booking.
     * Enforces:
     * 1. Non-empty list.
     * 2. No duplicate seat selections within the request.
     * 3. Valid seat code syntax and bounds (A1-E8).
     * 4. Seat availability (not already booked).
     *
     * @param show      target show
     * @param seatCodes requested seat codes
     * @throws BookingException if any validation check fails
     */
    public void validateSeatCodes(Show show, List<String> seatCodes) throws BookingException {
        if (show == null) {
            throw new BookingException("Movie show cannot be null.");
        }
        if (seatCodes == null || seatCodes.isEmpty()) {
            throw new BookingException("Please enter at least one seat code.");
        }

        Set<String> seenCodes = new HashSet<>();
        for (String code : seatCodes) {
            if (code == null || code.trim().isEmpty()) {
                throw new BookingException("Empty seat code detected in selection.");
            }
            String upperCode = code.trim().toUpperCase();

            // Check duplicate selection within the same request
            if (!seenCodes.add(upperCode)) {
                throw new BookingException("Duplicate seat selection: " + upperCode + " was entered more than once.");
            }

            // Check validity of seat code format and grid bounds
            if (!show.isValidSeatCode(upperCode)) {
                throw new BookingException("Invalid seat code: '" + upperCode + "'. Valid seat codes range from A1 to E8.");
            }

            // Check if already booked
            Seat seat = show.getSeat(upperCode);
            if (seat == null || seat.isBooked()) {
                throw new BookingException("Seat " + upperCode + " is already booked or unavailable.");
            }
        }
    }

    /**
     * Atomically books seats for a given show, applies promotional coupons,
     * updates seat states, and logs the booking record.
     *
     * @param show       target show
     * @param rawCodes   list of seat code strings
     * @param couponCode optional promotional coupon (e.g. "CINE10")
     * @return the created Booking record
     * @throws BookingException if validation fails or booking cannot be completed
     */
    public Booking bookTickets(Show show, List<String> rawCodes, String couponCode) throws BookingException {
        // Standardize codes
        List<String> normalizedCodes = new ArrayList<>();
        if (rawCodes != null) {
            for (String c : rawCodes) {
                if (c != null && !c.trim().isEmpty()) {
                    normalizedCodes.add(c.trim().toUpperCase());
                }
            }
        }

        // Validate all seats before modifying any state (Atomic validation)
        validateSeatCodes(show, normalizedCodes);

        // Calculate pricing using overloaded Show methods (FR-6)
        String[] codeArray = normalizedCodes.toArray(new String[0]);
        double finalAmount = show.calculateTotal(codeArray, couponCode);

        // Execute atomic seat booking on the Show (Bookable interface)
        boolean bookedSuccessfully = show.bookSeats(normalizedCodes);
        if (!bookedSuccessfully) {
            throw new BookingException("Unable to book selected seats. Another transaction may have completed first.");
        }

        // Create booking record and store in theatre
        Booking booking = new Booking(show, normalizedCodes, finalAmount);
        theatre.addBooking(booking);

        return booking;
    }

    /**
     * Cancels an existing booking by its unique ID.
     * Releases booked seats in the associated show, calculates an 80% refund,
     * and marks the booking as cancelled.
     *
     * @param bookingId unique booking identifier (e.g. "BK1001")
     * @return the cancelled Booking record
     * @throws BookingException if booking is not found or already cancelled
     */
    public Booking cancelBooking(String bookingId) throws BookingException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new BookingException("Booking ID cannot be empty.");
        }

        Booking booking = theatre.findBookingById(bookingId.trim());
        if (booking == null) {
            throw new BookingException("No booking found with ID: " + bookingId.trim().toUpperCase());
        }

        if (booking.isCancelled()) {
            throw new BookingException("Booking " + booking.getBookingId() + " has already been cancelled.");
        }

        // Release seats on the associated show
        boolean released = booking.getShow().cancelBooking(booking.getSeatCodes());
        if (!released) {
            throw new BookingException("Failed to release seats for booking " + booking.getBookingId());
        }

        // Mark as cancelled
        booking.setCancelled(true);
        return booking;
    }
}

