package com.cinebook.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a customer booking record.
 * Demonstrates:
 * - Static members: bookingCounter generating incremental unique booking IDs.
 * - Composition: Booking references a Show and a list of seat codes.
 * - Encapsulation: Private state with accessors.
 */
public class Booking {
    // Static counter initialized to 1000 to generate IDs like BK1001, BK1002
    private static int bookingCounter = 1000;

    private final String bookingId;
    private final Show show;
    private final List<String> seatCodes;
    private final double totalAmount;
    private boolean cancelled;

    /**
     * Constructs a new Booking and automatically assigns a unique ID.
     *
     * @param show        the Show instance associated with this booking
     * @param seatCodes   list of booked seat codes (e.g., ["C3", "C4"])
     * @param totalAmount final amount paid after applicable discounts
     */
    public Booking(Show show, List<String> seatCodes, double totalAmount) {
        this.bookingId = generateNextBookingId();
        this.show = show;
        this.seatCodes = new ArrayList<>(seatCodes);
        this.totalAmount = totalAmount;
        this.cancelled = false;
    }

    /**
     * Generates a unique sequential booking identifier.
     * Synchronized to guarantee uniqueness in any concurrent context.
     *
     * @return generated ID (e.g. "BK1001")
     */
    private static synchronized String generateNextBookingId() {
        bookingCounter++;
        return "BK" + bookingCounter;
    }

    /**
     * Resets counter for testing purposes if required.
     */
    public static synchronized void resetCounterForTesting() {
        bookingCounter = 1000;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Show getShow() {
        return show;
    }

    public List<String> getSeatCodes() {
        return Collections.unmodifiableList(seatCodes);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Calculates the 80% refund based on the actual amount paid.
     *
     * @return 80% of totalAmount
     */
    public double calculateRefundAmount() {
        return totalAmount * 0.80;
    }

    @Override
    public String toString() {
        return String.format("[%s] Movie: %s | Time: %s | Seats: %s | Paid: Rs. %.2f | Status: %s",
                bookingId,
                show.getMovieName(),
                show.getShowTime(),
                String.join(", ", seatCodes),
                totalAmount,
                cancelled ? "CANCELLED" : "CONFIRMED");
    }
}

