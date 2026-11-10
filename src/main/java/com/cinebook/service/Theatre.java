package com.cinebook.service;

import com.cinebook.model.Booking;
import com.cinebook.model.Show;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the cinema theatre facility managing shows and booking history.
 * Demonstrates:
 * - Composition: Theatre contains a collection of Show objects and Booking records.
 * - ArrayList: Uses dynamic lists for data storage.
 * - Encapsulation: Protects internal collections with unmodifiable views and helper accessors.
 */
public class Theatre {
    private final List<Show> shows;
    private final List<Booking> bookings;

    /**
     * Constructs a Theatre with default movie shows.
     */
    public Theatre() {
        this.shows = new ArrayList<>();
        this.bookings = new ArrayList<>();
        initializeDefaultShows();
    }

    /**
     * Populates the theatre with initial screenings.
     * Guaranteed to have at least three shows as required by FR-1.
     */
    private void initializeDefaultShows() {
        shows.add(new Show("Interstellar", "7:30 PM"));
        shows.add(new Show("Inception", "4:00 PM"));
        shows.add(new Show("The Dark Knight", "9:00 PM"));
    }

    /**
     * Adds an additional movie show to the theatre.
     *
     * @param show the show to add
     */
    public void addShow(Show show) {
        if (show != null) {
            shows.add(show);
        }
    }

    /**
     * Returns an unmodifiable list of all shows.
     *
     * @return list of shows
     */
    public List<Show> getShows() {
        return Collections.unmodifiableList(shows);
    }

    /**
     * Finds a show by its 1-based index (as displayed in the menu).
     *
     * @param oneBasedIndex 1-based index (e.g., 1 for the first show)
     * @return Show if valid index; null otherwise
     */
    public Show getShowByIndex(int oneBasedIndex) {
        int zeroBased = oneBasedIndex - 1;
        if (zeroBased >= 0 && zeroBased < shows.size()) {
            return shows.get(zeroBased);
        }
        return null;
    }

    /**
     * Stores a confirmed booking in the theatre records.
     *
     * @param booking the confirmed booking to save
     */
    public void addBooking(Booking booking) {
        if (booking != null) {
            bookings.add(booking);
        }
    }

    /**
     * Searches for a booking by its unique booking ID (case-insensitive).
     *
     * @param bookingId booking ID (e.g., "BK1001")
     * @return Booking instance if found; null otherwise
     */
    public Booking findBookingById(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            return null;
        }
        String searchId = bookingId.trim().toUpperCase();
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(searchId)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Returns an unmodifiable list of all booking records.
     *
     * @return list of bookings
     */
    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }
}

