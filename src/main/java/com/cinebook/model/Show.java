package com.cinebook.model;

import com.cinebook.interfaces.Bookable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an individual movie showtime in the cinema.
 * Demonstrates:
 * - Abstraction & Polymorphism: Implements the Bookable interface.
 * - Encapsulation: Protects internal 2D Seat array state.
 * - 2D Arrays: Maintains an independent 5x8 grid of seats.
 * - Method Overloading: calculateTotal() with and without discount coupons.
 */
public class Show implements Bookable {
    public static final int NUM_ROWS = 5;
    public static final int NUM_COLS = 8;
    public static final String COUPON_CINE10 = "CINE10";
    public static final double CINE10_DISCOUNT_PERCENT = 10.0;

    private final String movieName;
    private final String showTime;
    private final Seat[][] seats;

    /**
     * Constructs a Show with a movie name, showtime, and initializes its independent 5x8 seat grid.
     *
     * @param movieName title of the movie
     * @param showTime  time of the screening (e.g., "7:30 PM")
     */
    public Show(String movieName, String showTime) {
        this.movieName = movieName;
        this.showTime = showTime;
        this.seats = new Seat[NUM_ROWS][NUM_COLS];
        initializeSeatGrid();
    }

    /**
     * Initializes the 5x8 seat grid with independent Seat objects.
     * Rows:
     * - A & B (indices 0, 1): REGULAR
     * - C & D (indices 2, 3): PREMIUM
     * - E (index 4): VIP
     */
    private void initializeSeatGrid() {
        for (int r = 0; r < NUM_ROWS; r++) {
            char rowChar = (char) ('A' + r);
            SeatType type;
            if (r < 2) {
                type = SeatType.REGULAR;
            } else if (r < 4) {
                type = SeatType.PREMIUM;
            } else {
                type = SeatType.VIP;
            }

            for (int c = 0; c < NUM_COLS; c++) {
                String seatCode = "" + rowChar + (c + 1);
                seats[r][c] = new Seat(seatCode, type);
            }
        }
    }

    public String getMovieName() {
        return movieName;
    }

    public String getShowTime() {
        return showTime;
    }

    /**
     * Returns the 2D array of seats.
     *
     * @return 5x8 Seat array
     */
    public Seat[][] getSeats() {
        return seats;
    }

    /**
     * Checks if a seat code is syntactically valid and exists within the 5x8 grid.
     * Valid format: Rows A-E, Columns 1-8 (e.g., A1, C8, E5).
     *
     * @param seatCode the seat code string to validate
     * @return true if valid; false otherwise
     */
    public boolean isValidSeatCode(String seatCode) {
        if (seatCode == null) {
            return false;
        }
        String trimmed = seatCode.trim().toUpperCase();
        if (trimmed.length() != 2) {
            return false;
        }
        char row = trimmed.charAt(0);
        char col = trimmed.charAt(1);
        return (row >= 'A' && row <= 'E') && (col >= '1' && col <= '8');
    }

    /**
     * Retrieves the Seat object corresponding to the given seat code.
     *
     * @param seatCode e.g., "A1", "C4"
     * @return Seat object if found, or null if invalid
     */
    public Seat getSeat(String seatCode) {
        if (!isValidSeatCode(seatCode)) {
            return null;
        }
        String trimmed = seatCode.trim().toUpperCase();
        int rowIndex = trimmed.charAt(0) - 'A';
        int colIndex = trimmed.charAt(1) - '1';
        return seats[rowIndex][colIndex];
    }

    /**
     * Checks whether all given seats are valid and unbooked, without making any modifications.
     *
     * @param seatCodes list of seat codes to check
     * @return true if all seats exist and are available; false otherwise
     */
    public boolean areSeatsAvailable(List<String> seatCodes) {
        if (seatCodes == null || seatCodes.isEmpty()) {
            return false;
        }
        for (String code : seatCodes) {
            Seat seat = getSeat(code);
            if (seat == null || seat.isBooked()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the raw total ticket price for the specified seats without discounts.
     * Demonstrates: Method Overloading (Method 1 using String[] as specified in FR-6).
     *
     * @param seatCodes array of seat codes
     * @return total price based on SeatType enum prices
     */
    public double calculateTotal(String[] seatCodes) {
        if (seatCodes == null) {
            return 0.0;
        }
        double total = 0.0;
        for (String code : seatCodes) {
            Seat seat = getSeat(code);
            if (seat != null) {
                total += seat.getSeatType().getPrice();
            }
        }
        return total;
    }

    /**
     * Calculates the total ticket price for the specified seats applying a promotional coupon.
     * Demonstrates: Method Overloading (Method 2 using String[] and couponCode as specified in FR-6).
     *
     * @param seatCodes  array of seat codes
     * @param couponCode promotional coupon code (e.g., "CINE10")
     * @return discounted total if coupon is valid; raw total otherwise
     */
    public double calculateTotal(String[] seatCodes, String couponCode) {
        double subtotal = calculateTotal(seatCodes);
        if (couponCode != null && couponCode.trim().equalsIgnoreCase(COUPON_CINE10)) {
            double discount = (subtotal * CINE10_DISCOUNT_PERCENT) / 100.0;
            return subtotal - discount;
        }
        return subtotal;
    }

    /**
     * Overloaded helper: Calculates total for a List of seat codes.
     *
     * @param seatCodes list of seat codes
     * @return total price
     */
    public double calculateTotal(List<String> seatCodes) {
        if (seatCodes == null) {
            return 0.0;
        }
        return calculateTotal(seatCodes.toArray(new String[0]));
    }

    /**
     * Overloaded helper: Calculates total for a List of seat codes with coupon.
     *
     * @param seatCodes  list of seat codes
     * @param couponCode coupon code
     * @return total price
     */
    public double calculateTotal(List<String> seatCodes, String couponCode) {
        if (seatCodes == null) {
            return 0.0;
        }
        return calculateTotal(seatCodes.toArray(new String[0]), couponCode);
    }

    /**
     * Calculates the discount amount in Rupees for a given coupon.
     *
     * @param seatCodes  list of seat codes
     * @param couponCode coupon code
     * @return discount amount in Rs.
     */
    public double calculateDiscount(List<String> seatCodes, String couponCode) {
        if (couponCode != null && couponCode.trim().equalsIgnoreCase(COUPON_CINE10)) {
            double original = calculateTotal(seatCodes);
            return (original * CINE10_DISCOUNT_PERCENT) / 100.0;
        }
        return 0.0;
    }

    /**
     * Books multiple seats atomically.
     * If any seat is invalid or already booked, the entire transaction is rejected.
     * Implements Bookable interface.
     *
     * @param seatCodes list of seat codes to book
     * @return true if all seats were successfully booked; false otherwise
     */
    @Override
    public boolean bookSeats(List<String> seatCodes) {
        if (!areSeatsAvailable(seatCodes)) {
            return false;
        }
        // Atomic booking: Mark all as booked only after full validation passes
        for (String code : seatCodes) {
            Seat seat = getSeat(code);
            seat.setBooked(true);
        }
        return true;
    }

    /**
     * Cancels bookings and frees the specified seats.
     * Implements Bookable interface.
     *
     * @param seatCodes list of seat codes to free
     * @return true if operation was executed
     */
    @Override
    public boolean cancelBooking(List<String> seatCodes) {
        if (seatCodes == null || seatCodes.isEmpty()) {
            return false;
        }
        for (String code : seatCodes) {
            Seat seat = getSeat(code);
            if (seat != null) {
                seat.setBooked(false);
            }
        }
        return true;
    }

    /**
     * Counts how many seats are currently available in this show.
     *
     * @return available seat count
     */
    public int getAvailableSeatCount() {
        int count = 0;
        for (int r = 0; r < NUM_ROWS; r++) {
            for (int c = 0; c < NUM_COLS; c++) {
                if (!seats[r][c].isBooked()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Retrieves unique categories present among the selected seat codes.
     *
     * @param seatCodes list of seat codes
     * @return formatted string of category names (e.g. "Premium", "Regular, VIP")
     */
    public String getCategoriesForSeats(List<String> seatCodes) {
        if (seatCodes == null || seatCodes.isEmpty()) {
            return "N/A";
        }
        List<String> categories = new ArrayList<>();
        for (String code : seatCodes) {
            Seat seat = getSeat(code);
            if (seat != null) {
                String catName = seat.getSeatType().toString();
                if (!categories.contains(catName)) {
                    categories.add(catName);
                }
            }
        }
        return String.join(", ", categories);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (Available: %d/%d)", 
                movieName, showTime, getAvailableSeatCount(), NUM_ROWS * NUM_COLS);
    }
}

