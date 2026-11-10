package com.cinebook.model;

/**
 * Represents an individual seat in a cinema auditorium.
 * Demonstrates: Encapsulation (private fields with getters and setters).
 */
public class Seat {
    private final String seatCode;
    private final SeatType seatType;
    private boolean booked;

    /**
     * Constructs a new Seat with the specified code and type.
     * Newly created seats are unbooked by default.
     *
     * @param seatCode the alphanumeric seat identifier (e.g., "A1", "C4")
     * @param seatType the category of the seat (REGULAR, PREMIUM, VIP)
     */
    public Seat(String seatCode, SeatType seatType) {
        this.seatCode = seatCode;
        this.seatType = seatType;
        this.booked = false;
    }

    /**
     * Gets the seat code.
     *
     * @return seat code
     */
    public String getSeatCode() {
        return seatCode;
    }

    /**
     * Gets the seat category type.
     *
     * @return seat type
     */
    public SeatType getSeatType() {
        return seatType;
    }

    /**
     * Checks if the seat is currently booked.
     *
     * @return true if booked, false if available
     */
    public boolean isBooked() {
        return booked;
    }

    /**
     * Updates the booking status of the seat.
     *
     * @param booked new booking status
     */
    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", seatCode, seatType, booked ? "Booked" : "Available");
    }
}

