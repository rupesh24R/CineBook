package com.cinebook.interfaces;

import java.util.List;

/**
 * Interface defining contract for bookable cinema resources (e.g., Show).
 * Demonstrates: Abstraction and Polymorphism.
 */
public interface Bookable {

    /**
     * Attempts to book the specified list of seat codes atomically.
     *
     * @param seatCodes list of seat codes to book
     * @return true if all seats were successfully booked; false otherwise
     */
    boolean bookSeats(List<String> seatCodes);

    /**
     * Cancels bookings for the specified list of seat codes and frees them.
     *
     * @param seatCodes list of seat codes to cancel
     * @return true if seats were successfully released; false otherwise
     */
    boolean cancelBooking(List<String> seatCodes);
}

