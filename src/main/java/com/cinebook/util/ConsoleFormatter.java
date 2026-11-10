package com.cinebook.util;

import com.cinebook.model.Booking;
import com.cinebook.model.Seat;
import com.cinebook.model.SeatType;
import com.cinebook.model.Show;
import java.util.List;

/**
 * Utility class dedicated to styling, borders, tables, and presentation on the terminal console.
 * Demonstrates: Separation of concerns (UI formatting isolated from business logic).
 */
public class ConsoleFormatter {
    public static final String LINE_SEPARATOR_40 = "========================================";
    public static final String LINE_SEPARATOR_32 = "================================";

    private ConsoleFormatter() {
    }

    /**
     * Formats a monetary amount to standard "Rs. XX.XX" format.
     *
     * @param amount monetary value
     * @return formatted currency string
     */
    public static String formatCurrency(double amount) {
        return String.format("Rs. %.2f", amount);
    }

    /**
     * Prints the primary application banner.
     */
    public static void printAppHeader() {
        System.out.println(LINE_SEPARATOR_40);
        System.out.println("               CINEBOOK");
        System.out.println("       MOVIE TICKET BOOKING SYSTEM");
        System.out.println(LINE_SEPARATOR_40);
    }

    /**
     * Prints the main navigation menu options.
     */
    public static void printMainMenu() {
        System.out.println("\n" + LINE_SEPARATOR_40);
        System.out.println("             MAIN MENU");
        System.out.println(LINE_SEPARATOR_40);
        System.out.println("1. View Movies and Shows");
        System.out.println("2. View Seat Availability");
        System.out.println("3. Book Movie Tickets");
        System.out.println("4. View Booking Details");
        System.out.println("5. Cancel Booking");
        System.out.println("6. View All Bookings");
        System.out.println("7. Exit");
        System.out.println(LINE_SEPARATOR_40);
        System.out.print("Enter your choice: ");
    }

    /**
     * Prints a formatted list of all active movie shows.
     *
     * @param shows list of shows
     */
    public static void printShowList(List<Show> shows) {
        System.out.println("\n" + LINE_SEPARATOR_40);
        System.out.println("           AVAILABLE SHOWS");
        System.out.println(LINE_SEPARATOR_40);
        if (shows == null || shows.isEmpty()) {
            System.out.println("No shows currently scheduled.");
            return;
        }

        for (int i = 0; i < shows.size(); i++) {
            Show show = shows.get(i);
            System.out.printf("%d. %s – %s  [%d seats available]%n",
                    (i + 1),
                    show.getMovieName(),
                    show.getShowTime(),
                    show.getAvailableSeatCount());
        }
    }

    /**
     * Displays a clean, formatted 5x8 seat availability map for a specific show.
     * Rows: A-B (Regular, Rs 150), C-D (Premium, Rs 250), E (VIP, Rs 400).
     *
     * @param show the show whose seat map to display
     */
    public static void printSeatMap(Show show) {
        if (show == null) {
            System.out.println("Show details not found.");
            return;
        }

        System.out.println("\n" + LINE_SEPARATOR_40);
        System.out.printf("  SEAT MAP: %s (%s)%n", show.getMovieName(), show.getShowTime());
        System.out.println(LINE_SEPARATOR_40);
        System.out.println("\n              SCREEN\n");
        System.out.println("       1  2  3  4  5  6  7  8");

        Seat[][] seats = show.getSeats();
        for (int r = 0; r < Show.NUM_ROWS; r++) {
            char rowChar = (char) ('A' + r);
            StringBuilder rowStr = new StringBuilder();
            rowStr.append(rowChar).append("      ");
            for (int c = 0; c < Show.NUM_COLS; c++) {
                char symbol = seats[r][c].isBooked() ? 'X' : 'O';
                rowStr.append(symbol).append("  ");
            }
            System.out.println(rowStr.toString());
        }

        System.out.println("\nLegend:  O = Available  |  X = Booked");
        System.out.printf("Pricing: Rows A-B : %s (%s)%n", SeatType.REGULAR, formatCurrency(SeatType.REGULAR.getPrice()));
        System.out.printf("         Rows C-D : %s (%s)%n", SeatType.PREMIUM, formatCurrency(SeatType.PREMIUM.getPrice()));
        System.out.printf("         Row  E   : %s (%s)%n", SeatType.VIP, formatCurrency(SeatType.VIP.getPrice()));
        System.out.println(LINE_SEPARATOR_40);
    }

    /**
     * Prints the official booking confirmation summary card (FR-5).
     *
     * @param booking the confirmed booking record
     */
    public static void printBookingSummary(Booking booking) {
        if (booking == null) {
            return;
        }
        Show show = booking.getShow();
        String seatsList = String.join(", ", booking.getSeatCodes());
        String categories = show.getCategoriesForSeats(booking.getSeatCodes());

        System.out.println("\n" + LINE_SEPARATOR_32);
        System.out.println("          BOOKING CONFIRMED");
        System.out.println(LINE_SEPARATOR_32 + "\n");
        System.out.printf("Booking ID : %s%n", booking.getBookingId());
        System.out.printf("Movie      : %s%n", show.getMovieName());
        System.out.printf("Show Time  : %s%n", show.getShowTime());
        System.out.printf("Seats      : %s%n", seatsList);
        System.out.printf("Category   : %s%n", categories);
        System.out.printf("Total      : %s%n", formatCurrency(booking.getTotalAmount()));
        System.out.println("Status     : CONFIRMED\n");
        System.out.println(LINE_SEPARATOR_32);
    }

    /**
     * Prints the cancellation receipt summary (FR-7).
     *
     * @param booking the cancelled booking record
     */
    public static void printCancellationSummary(Booking booking) {
        if (booking == null) {
            return;
        }

        System.out.println("\n" + LINE_SEPARATOR_32);
        System.out.println("       CANCELLATION SUCCESSFUL");
        System.out.println(LINE_SEPARATOR_32 + "\n");
        System.out.printf("Booking ID : %s%n", booking.getBookingId());
        System.out.printf("Original   : %s%n", formatCurrency(booking.getTotalAmount()));
        System.out.printf("Refund     : %s%n", formatCurrency(booking.calculateRefundAmount()));
        System.out.println("Status     : CANCELLED\n");
        System.out.println(LINE_SEPARATOR_32);
    }

    /**
     * Prints single booking details view.
     *
     * @param booking booking record
     */
    public static void printBookingDetails(Booking booking) {
        if (booking == null) {
            System.out.println("No booking record provided.");
            return;
        }
        Show show = booking.getShow();
        String seatsList = String.join(", ", booking.getSeatCodes());
        String categories = show.getCategoriesForSeats(booking.getSeatCodes());

        System.out.println("\n" + LINE_SEPARATOR_40);
        System.out.println("           BOOKING DETAILS");
        System.out.println(LINE_SEPARATOR_40);
        System.out.printf("Booking ID  : %s%n", booking.getBookingId());
        System.out.printf("Status      : %s%n", booking.isCancelled() ? "CANCELLED" : "CONFIRMED");
        System.out.printf("Movie       : %s%n", show.getMovieName());
        System.out.printf("Show Time   : %s%n", show.getShowTime());
        System.out.printf("Seats       : %s%n", seatsList);
        System.out.printf("Category    : %s%n", categories);
        System.out.printf("Amount Paid : %s%n", formatCurrency(booking.getTotalAmount()));
        if (booking.isCancelled()) {
            System.out.printf("Refund (80%%): %s%n", formatCurrency(booking.calculateRefundAmount()));
        }
        System.out.println(LINE_SEPARATOR_40);
    }

    /**
     * Prints the table of all recorded bookings in the system.
     *
     * @param bookings list of all bookings
     */
    public static void printAllBookings(List<Booking> bookings) {
        System.out.println("\n" + LINE_SEPARATOR_40);
        System.out.println("             ALL BOOKINGS");
        System.out.println(LINE_SEPARATOR_40);
        if (bookings == null || bookings.isEmpty()) {
            System.out.println("No bookings have been made yet.");
            return;
        }

        System.out.printf("%-10s %-16s %-10s %-12s %-12s %-10s%n",
                "ID", "Movie", "Time", "Seats", "Amount", "Status");
        System.out.println("----------------------------------------------------------------------");

        for (Booking b : bookings) {
            String seats = String.join(",", b.getSeatCodes());
            if (seats.length() > 10) {
                seats = seats.substring(0, 9) + "…";
            }
            String movie = b.getShow().getMovieName();
            if (movie.length() > 15) {
                movie = movie.substring(0, 14) + "…";
            }
            System.out.printf("%-10s %-16s %-10s %-12s %-12s %-10s%n",
                    b.getBookingId(),
                    movie,
                    b.getShow().getShowTime(),
                    seats,
                    formatCurrency(b.getTotalAmount()),
                    b.isCancelled() ? "CANCELLED" : "CONFIRMED");
        }
        System.out.println(LINE_SEPARATOR_40);
    }
}

