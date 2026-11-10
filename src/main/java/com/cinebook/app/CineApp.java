package com.cinebook.app;

import com.cinebook.model.Booking;
import com.cinebook.model.Show;
import com.cinebook.service.BookingService;
import com.cinebook.service.Theatre;
import com.cinebook.util.BookingException;
import com.cinebook.util.ConsoleFormatter;
import com.cinebook.util.InputValidator;
import java.util.List;
import java.util.Scanner;

/**
 * Controller class managing the user interface and interactions for the CineBook console application.
 * Demonstrates:
 * - Separation of Concerns: Handles I/O and user flows while delegating business rules to BookingService.
 * - Defensive Programming: Validates inputs to prevent unexpected application crashes.
 */
public class CineApp {
    private final Theatre theatre;
    private final BookingService bookingService;
    private final Scanner scanner;
    private boolean running;

    /**
     * Initializes CineApp with its service dependencies and console scanner.
     */
    public CineApp() {
        this.theatre = new Theatre();
        this.bookingService = new BookingService(theatre);
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    /**
     * Overloaded constructor accepting an external Scanner for testing or custom streams.
     *
     * @param scanner custom scanner instance
     */
    public CineApp(Scanner scanner) {
        this.theatre = new Theatre();
        this.bookingService = new BookingService(theatre);
        this.scanner = scanner;
        this.running = true;
    }

    /**
     * Main application loop.
     */
    public void start() {
        ConsoleFormatter.printAppHeader();

        while (running) {
            ConsoleFormatter.printMainMenu();
            String rawChoice = scanner.nextLine();
            Integer choice = InputValidator.parseInteger(rawChoice);

            if (!InputValidator.isValidMenuChoice(choice, 1, 7)) {
                System.out.println("\n[ERROR] Invalid choice. Please enter a number between 1 and 7.");
                continue;
            }

            switch (choice) {
                case 1:
                    handleViewShows();
                    break;
                case 2:
                    handleViewSeatAvailability();
                    break;
                case 3:
                    handleBookTickets();
                    break;
                case 4:
                    handleViewBookingDetails();
                    break;
                case 5:
                    handleCancelBooking();
                    break;
                case 6:
                    handleViewAllBookings();
                    break;
                case 7:
                    handleExit();
                    break;
                default:
                    System.out.println("\n[ERROR] Unexpected selection.");
            }
        }
    }

    /**
     * Menu Option 1: Displays list of movies and shows.
     */
    private void handleViewShows() {
        ConsoleFormatter.printShowList(theatre.getShows());
        pauseForUser();
    }

    /**
     * Menu Option 2: Displays seat availability map for a selected show.
     */
    private void handleViewSeatAvailability() {
        Show show = promptShowSelection();
        if (show == null) {
            return;
        }
        ConsoleFormatter.printSeatMap(show);
        pauseForUser();
    }

    /**
     * Menu Option 3: Guided workflow for booking movie tickets.
     */
    private void handleBookTickets() {
        System.out.println("\n--- BOOK MOVIE TICKETS ---");
        Show show = promptShowSelection();
        if (show == null) {
            return;
        }

        // Display current seat map
        ConsoleFormatter.printSeatMap(show);

        if (show.getAvailableSeatCount() == 0) {
            System.out.println("\n[NOTICE] This show is completely sold out! Please select another show.");
            pauseForUser();
            return;
        }

        // Prompt for seat codes
        System.out.println("Enter seat code(s) separated by commas (e.g., C3,C4,E2): ");
        System.out.print("Seat selection: ");
        String seatInput = scanner.nextLine();
        List<String> seatCodes = InputValidator.parseSeatCodes(seatInput);

        // Pre-validate seats
        try {
            bookingService.validateSeatCodes(show, seatCodes);
        } catch (BookingException e) {
            System.out.println("\n[BOOKING FAILED] " + e.getMessage());
            System.out.println("No seats were booked. Please try again.");
            pauseForUser();
            return;
        }

        // Pricing calculation (Method 1: calculateTotal without coupon)
        double originalTotal = show.calculateTotal(seatCodes);
        System.out.printf("%nOriginal Total for %d seat(s): %s%n",
                seatCodes.size(), ConsoleFormatter.formatCurrency(originalTotal));

        // Prompt for coupon
        System.out.print("Enter promotional coupon (or press Enter to skip): ");
        String couponInput = scanner.nextLine().trim();

        String appliedCoupon = null;
        if (!couponInput.isEmpty()) {
            if (couponInput.equalsIgnoreCase(Show.COUPON_CINE10)) {
                appliedCoupon = couponInput.toUpperCase();
                double discount = show.calculateDiscount(seatCodes, appliedCoupon);
                System.out.printf("[SUCCESS] Coupon 'CINE10' applied! Discount: %s (10%% off)%n",
                        ConsoleFormatter.formatCurrency(discount));
            } else {
                System.out.printf("[NOTICE] Coupon '%s' is invalid or expired. No discount applied.%n", couponInput);
            }
        }

        // Calculate final total (Method 2: calculateTotal with coupon)
        double finalTotal = show.calculateTotal(seatCodes, appliedCoupon);
        System.out.printf("Final Total to Pay: %s%n", ConsoleFormatter.formatCurrency(finalTotal));

        // Booking confirmation prompt
        System.out.print("Confirm booking? (Y/N): ");
        String confirmation = scanner.nextLine().trim();
        if (!confirmation.equalsIgnoreCase("Y") && !confirmation.equalsIgnoreCase("YES")) {
            System.out.println("\n[NOTICE] Booking cancelled by customer. No seats were reserved.");
            pauseForUser();
            return;
        }

        // Execute booking atomically
        try {
            Booking booking = bookingService.bookTickets(show, seatCodes, appliedCoupon);
            ConsoleFormatter.printBookingSummary(booking);
        } catch (BookingException e) {
            System.out.println("\n[ERROR] Could not complete booking: " + e.getMessage());
        }

        pauseForUser();
    }

    /**
     * Menu Option 4: Displays details of a specific booking by ID.
     */
    private void handleViewBookingDetails() {
        System.out.print("\nEnter Booking ID (e.g., BK1001): ");
        String bookingId = scanner.nextLine().trim();
        if (bookingId.isEmpty()) {
            System.out.println("[ERROR] Booking ID cannot be empty.");
            pauseForUser();
            return;
        }

        Booking booking = theatre.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("\n[ERROR] No booking found with ID: " + bookingId.toUpperCase());
        } else {
            ConsoleFormatter.printBookingDetails(booking);
        }
        pauseForUser();
    }

    /**
     * Menu Option 5: Cancels an existing booking and computes 80% refund.
     */
    private void handleCancelBooking() {
        System.out.println("\n--- CANCEL BOOKING ---");
        System.out.print("Enter Booking ID to cancel: ");
        String bookingId = scanner.nextLine().trim();

        if (bookingId.isEmpty()) {
            System.out.println("[ERROR] Booking ID cannot be empty.");
            pauseForUser();
            return;
        }

        try {
            Booking booking = bookingService.cancelBooking(bookingId);
            ConsoleFormatter.printCancellationSummary(booking);
        } catch (BookingException e) {
            System.out.println("\n[CANCELLATION FAILED] " + e.getMessage());
        }

        pauseForUser();
    }

    /**
     * Menu Option 6: Displays all bookings made in the current session.
     */
    private void handleViewAllBookings() {
        ConsoleFormatter.printAllBookings(theatre.getBookings());
        pauseForUser();
    }

    /**
     * Menu Option 7: Terminates application.
     */
    private void handleExit() {
        System.out.println("\n========================================");
        System.out.println("   Thank you for using CineBook!");
        System.out.println("     Enjoy your movie experience!");
        System.out.println("========================================\n");
        running = false;
    }

    /**
     * Prompts customer to select an available show from the list.
     * Retries upon invalid selection. Returns null if user cancels or enters empty.
     *
     * @return selected Show or null
     */
    private Show promptShowSelection() {
        List<Show> shows = theatre.getShows();
        ConsoleFormatter.printShowList(shows);

        while (true) {
            System.out.printf("Select a show (1-%d) or '0' to return to Main Menu: ", shows.size());
            String input = scanner.nextLine().trim();
            Integer index = InputValidator.parseInteger(input);

            if (index != null && index == 0) {
                return null;
            }

            if (InputValidator.isValidMenuChoice(index, 1, shows.size())) {
                return theatre.getShowByIndex(index);
            }

            System.out.printf("[ERROR] Invalid choice. Please enter a number between 1 and %d.%n", shows.size());
        }
    }

    /**
     * Simple prompt to pause execution until the user presses Enter.
     */
    private void pauseForUser() {
        System.out.print("\nPress Enter to continue...");
        try {
            scanner.nextLine();
        } catch (Exception ignored) {
        }
    }
}

