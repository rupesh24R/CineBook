package com.cinebook.test;

import com.cinebook.model.Booking;
import com.cinebook.model.Seat;
import com.cinebook.model.SeatType;
import com.cinebook.model.Show;
import com.cinebook.service.BookingService;
import com.cinebook.service.Theatre;
import com.cinebook.util.BookingException;
import com.cinebook.util.InputValidator;
import java.util.Arrays;
import java.util.List;

/**
 * Standalone Automated Test Runner for CineBook.
 * Validates all Functional Requirements (FR-1 through FR-8) and Edge Cases.
 * Runs on standard Java without requiring external dependencies or internet.
 */
public class CineBookTestRunner {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("        CINEBOOK AUTOMATED TEST SUITE             ");
        System.out.println("==================================================");

        runTest("FR-1: Show Listing (At least 3 shows initialized)", CineBookTestRunner::testShowListing);
        runTest("FR-2 & FR-3: Seat Grid 5x8 Dimensions and Row Categories", CineBookTestRunner::testSeatGridDimensionsAndCategories);
        runTest("FR-4: Single Seat Booking (Book A1 succeeds)", CineBookTestRunner::testBookSingleSeat);
        runTest("FR-4: Multiple Seat Booking (Book C3, C4 atomically)", CineBookTestRunner::testBookMultipleSeats);
        runTest("FR-4 & Validation: Reject Invalid Seat Codes (Z9, A0, F2)", CineBookTestRunner::testRejectInvalidSeatCodes);
        runTest("FR-4 & Validation: Reject Already Booked Seats", CineBookTestRunner::testRejectOccupiedSeat);
        runTest("FR-4 & Validation: Reject Duplicate Seats in Selection (C3,C3)", CineBookTestRunner::testRejectDuplicateSeats);
        runTest("FR-6: Coupon System CINE10 (10% discount)", CineBookTestRunner::testCouponCine10);
        runTest("FR-6: Coupon System Invalid Coupon (No discount applied)", CineBookTestRunner::testInvalidCoupon);
        runTest("FR-6: Method Overloading for calculateTotal()", CineBookTestRunner::testMethodOverloading);
        runTest("FR-7: Cancel Valid Booking (Freed seats & 80% refund)", CineBookTestRunner::testCancellationAndRefund);
        runTest("FR-7: Cancel Already Cancelled Booking Rejected", CineBookTestRunner::testDuplicateCancellationRejected);
        runTest("FR-7: Cancel Unknown Booking ID Rejected", CineBookTestRunner::testCancelUnknownBooking);
        runTest("FR-8: Independent Show Seat Maps (Show 1 vs Show 2)", CineBookTestRunner::testIndependentShowSeatMaps);
        runTest("FR-5: Unique Booking ID Generation (BK1001, BK1002)", CineBookTestRunner::testBookingIdGeneration);
        runTest("InputValidator: Parsing, Trimming, and Empty Checks", CineBookTestRunner::testInputValidator);
        runTest("Re-booking: Book Seats After Cancellation", CineBookTestRunner::testRebookAfterCancellation);

        System.out.println("==================================================");
        System.out.printf("Test Results: %d Passed, %d Failed, %d Total%n",
                testsPassed, testsFailed, (testsPassed + testsFailed));
        System.out.println("==================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void runTest(String testName, Runnable testMethod) {
        try {
            testMethod.run();
            System.out.printf("[PASS] %s%n", testName);
            testsPassed++;
        } catch (Throwable t) {
            System.out.printf("[FAIL] %s - Reason: %s%n", testName, t.getMessage());
            t.printStackTrace(System.out);
            testsFailed++;
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError(String.format("%s [Expected: %s, Actual: %s]", message, expected, actual));
    }

    private static void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(String.format("%s [Expected: %.2f, Actual: %.2f]", message, expected, actual));
        }
    }

    private static void testShowListing() {
        Theatre theatre = new Theatre();
        List<Show> shows = theatre.getShows();
        assertTrue(shows.size() >= 3, "Theatre must have at least 3 shows.");
        assertEquals("Interstellar", shows.get(0).getMovieName(), "First movie title mismatch");
        assertEquals("Inception", shows.get(1).getMovieName(), "Second movie title mismatch");
        assertEquals("The Dark Knight", shows.get(2).getMovieName(), "Third movie title mismatch");
    }

    private static void testSeatGridDimensionsAndCategories() {
        Show show = new Show("Test Movie", "12:00 PM");
        Seat[][] seats = show.getSeats();
        assertEquals(5, seats.length, "Must have 5 rows");
        assertEquals(8, seats[0].length, "Must have 8 columns");

        // Verify categories
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 8; c++) {
                Seat seat = seats[r][c];
                assertTrue(!seat.isBooked(), "Initial seats must be unbooked");
                if (r < 2) {
                    assertEquals(SeatType.REGULAR, seat.getSeatType(), "Row A-B must be REGULAR");
                    assertEquals(150.0, seat.getSeatType().getPrice(), 0.001, "Regular price must be 150");
                } else if (r < 4) {
                    assertEquals(SeatType.PREMIUM, seat.getSeatType(), "Row C-D must be PREMIUM");
                    assertEquals(250.0, seat.getSeatType().getPrice(), 0.001, "Premium price must be 250");
                } else {
                    assertEquals(SeatType.VIP, seat.getSeatType(), "Row E must be VIP");
                    assertEquals(400.0, seat.getSeatType().getPrice(), 0.001, "VIP price must be 400");
                }
            }
        }
    }

    private static void testBookSingleSeat() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        try {
            Booking booking = service.bookTickets(show, List.of("A1"), null);
            assertTrue(booking != null, "Booking should not be null");
            assertTrue(show.getSeat("A1").isBooked(), "Seat A1 should be marked booked");
            assertEquals(150.0, booking.getTotalAmount(), 0.001, "A1 price should be 150.0");
            assertEquals(39, show.getAvailableSeatCount(), "Available seats should be 39");
        } catch (BookingException e) {
            throw new AssertionError("Booking A1 should succeed: " + e.getMessage());
        }
    }

    private static void testBookMultipleSeats() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        try {
            Booking booking = service.bookTickets(show, Arrays.asList("C3", "C4"), null);
            assertTrue(booking != null, "Booking should succeed");
            assertTrue(show.getSeat("C3").isBooked(), "Seat C3 should be booked");
            assertTrue(show.getSeat("C4").isBooked(), "Seat C4 should be booked");
            // C3 and C4 are Premium (250 each = 500 total)
            assertEquals(500.0, booking.getTotalAmount(), 0.001, "Total should be 500.0");
        } catch (BookingException e) {
            throw new AssertionError("Booking C3, C4 should succeed: " + e.getMessage());
        }
    }

    private static void testRejectInvalidSeatCodes() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        String[] invalidCodes = {"Z9", "A0", "F2", "AA1", "A9", "1A"};
        for (String invalid : invalidCodes) {
            try {
                service.bookTickets(show, List.of(invalid), null);
                throw new AssertionError("Should have rejected invalid seat: " + invalid);
            } catch (BookingException expected) {
                // Expected exception
            }
        }
    }

    private static void testRejectOccupiedSeat() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        try {
            service.bookTickets(show, List.of("B2"), null);
            assertTrue(show.getSeat("B2").isBooked(), "B2 should be booked");

            // Attempt to book B2 again
            try {
                service.bookTickets(show, List.of("B2"), null);
                throw new AssertionError("Booking already occupied seat B2 must fail");
            } catch (BookingException expected) {
                // Expected
            }

            // Attempt to book atomic batch [B3, B2] where B2 is already booked
            try {
                service.bookTickets(show, Arrays.asList("B3", "B2"), null);
                throw new AssertionError("Atomic booking containing occupied seat must fail");
            } catch (BookingException expected) {
                // Expected
            }

            // Verify B3 was NOT booked due to atomic rollback
            assertTrue(!show.getSeat("B3").isBooked(), "B3 must remain unbooked after atomic failure");

        } catch (BookingException e) {
            throw new AssertionError("Initial booking failed: " + e.getMessage());
        }
    }

    private static void testRejectDuplicateSeats() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        try {
            service.bookTickets(show, Arrays.asList("C3", "C3"), null);
            throw new AssertionError("Duplicate seats in selection must be rejected");
        } catch (BookingException expected) {
            // Expected
            assertTrue(!show.getSeat("C3").isBooked(), "C3 should not be booked on duplicate rejection");
        }
    }

    private static void testCouponCine10() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        try {
            // C3 (250) + C4 (250) = 500. 10% off 500 = 50 discount => 450 total.
            Booking booking = service.bookTickets(show, Arrays.asList("C3", "C4"), "CINE10");
            assertEquals(450.0, booking.getTotalAmount(), 0.001, "CINE10 discount must be 10% (Rs. 450.00)");
        } catch (BookingException e) {
            throw new AssertionError("Booking with coupon failed: " + e.getMessage());
        }
    }

    private static void testInvalidCoupon() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        try {
            // C3 (250) + C4 (250) = 500. Unknown coupon "SAVE50" should NOT apply discount.
            Booking booking = service.bookTickets(show, Arrays.asList("C3", "C4"), "SAVE50");
            assertEquals(500.0, booking.getTotalAmount(), 0.001, "Invalid coupon must apply 0 discount (Rs. 500.00)");
        } catch (BookingException e) {
            throw new AssertionError("Booking failed: " + e.getMessage());
        }
    }

    private static void testMethodOverloading() {
        Show show = new Show("Test Movie", "10:00 AM");
        String[] seats = new String[]{"A1", "C1", "E1"}; // 150 + 250 + 400 = 800

        // Method 1: calculateTotal(String[] seatCodes)
        double totalWithoutCoupon = show.calculateTotal(seats);
        assertEquals(800.0, totalWithoutCoupon, 0.001, "calculateTotal(String[]) must return 800.0");

        // Method 2: calculateTotal(String[] seatCodes, String couponCode)
        double totalWithCine10 = show.calculateTotal(seats, "CINE10");
        assertEquals(720.0, totalWithCine10, 0.001, "calculateTotal(String[], CINE10) must return 720.0");

        double totalWithDummyCoupon = show.calculateTotal(seats, "DUMMY");
        assertEquals(800.0, totalWithDummyCoupon, 0.001, "calculateTotal with dummy coupon must return 800.0");
    }

    private static void testCancellationAndRefund() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        try {
            Booking booking = service.bookTickets(show, Arrays.asList("C3", "C4"), "CINE10");
            String bookingId = booking.getBookingId();
            assertEquals(450.0, booking.getTotalAmount(), 0.001, "Total amount paid");

            // Cancel
            Booking cancelled = service.cancelBooking(bookingId);
            assertTrue(cancelled.isCancelled(), "Status must be cancelled");
            assertEquals(360.0, cancelled.calculateRefundAmount(), 0.001, "Refund must be 80% of 450 = 360.0");

            // Verify seats are freed
            assertTrue(!show.getSeat("C3").isBooked(), "Seat C3 must be released");
            assertTrue(!show.getSeat("C4").isBooked(), "Seat C4 must be released");
            assertEquals(40, show.getAvailableSeatCount(), "All 40 seats should be available again");
        } catch (BookingException e) {
            throw new AssertionError("Cancellation test failed: " + e.getMessage());
        }
    }

    private static void testDuplicateCancellationRejected() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        try {
            Booking booking = service.bookTickets(show, List.of("A1"), null);
            service.cancelBooking(booking.getBookingId());

            // Second cancellation must throw BookingException
            try {
                service.cancelBooking(booking.getBookingId());
                throw new AssertionError("Second cancellation must be rejected");
            } catch (BookingException expected) {
                // Expected
            }
        } catch (BookingException e) {
            throw new AssertionError("Initial cancellation failed: " + e.getMessage());
        }
    }

    private static void testCancelUnknownBooking() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);

        try {
            service.cancelBooking("BK9999");
            throw new AssertionError("Cancelling non-existent booking must fail");
        } catch (BookingException expected) {
            // Expected
        }
    }

    private static void testIndependentShowSeatMaps() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show1 = theatre.getShows().get(0);
        Show show2 = theatre.getShows().get(1);

        try {
            // Book A1 in Show 1
            service.bookTickets(show1, List.of("A1"), null);
            assertTrue(show1.getSeat("A1").isBooked(), "Show 1 A1 must be booked");
            assertTrue(!show2.getSeat("A1").isBooked(), "Show 2 A1 must remain unbooked");

            // Book A1 in Show 2 must succeed independently
            Booking booking2 = service.bookTickets(show2, List.of("A1"), null);
            assertTrue(booking2 != null, "Show 2 A1 booking must succeed");
            assertTrue(show2.getSeat("A1").isBooked(), "Show 2 A1 is now booked");
        } catch (BookingException e) {
            throw new AssertionError("Independent seat maps test failed: " + e.getMessage());
        }
    }

    private static void testBookingIdGeneration() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        try {
            Booking b1 = service.bookTickets(show, List.of("D1"), null);
            Booking b2 = service.bookTickets(show, List.of("D2"), null);
            Booking b3 = service.bookTickets(show, List.of("D3"), null);

            assertTrue(b1.getBookingId().startsWith("BK"), "Booking ID should start with BK");
            assertTrue(b2.getBookingId().startsWith("BK"), "Booking ID should start with BK");
            assertTrue(b3.getBookingId().startsWith("BK"), "Booking ID should start with BK");

            int num1 = Integer.parseInt(b1.getBookingId().substring(2));
            int num2 = Integer.parseInt(b2.getBookingId().substring(2));
            int num3 = Integer.parseInt(b3.getBookingId().substring(2));

            assertEquals(num1 + 1, num2, "Booking IDs must increment sequentially");
            assertEquals(num2 + 1, num3, "Booking IDs must increment sequentially");
        } catch (BookingException e) {
            throw new AssertionError("Booking ID test failed: " + e.getMessage());
        }
    }

    private static void testInputValidator() {
        List<String> parsed = InputValidator.parseSeatCodes(" C3 , c4 ,  E2 , ");
        assertEquals(3, parsed.size(), "Should parse 3 seat codes");
        assertEquals("C3", parsed.get(0), "Should uppercase and trim C3");
        assertEquals("C4", parsed.get(1), "Should uppercase and trim C4");
        assertEquals("E2", parsed.get(2), "Should uppercase and trim E2");

        assertTrue(InputValidator.isValidSeatFormat("A1"), "A1 is valid format");
        assertTrue(InputValidator.isValidSeatFormat("e8"), "e8 is valid format");
        assertTrue(!InputValidator.isValidSeatFormat("Z9"), "Z9 is invalid format");
        assertTrue(!InputValidator.isValidSeatFormat(""), "empty is invalid format");

        assertEquals(Integer.valueOf(5), InputValidator.parseInteger("5"), "Should parse integer 5");
        assertEquals(null, InputValidator.parseInteger("abc"), "Non-numeric returns null");
    }

    private static void testRebookAfterCancellation() {
        Theatre theatre = new Theatre();
        BookingService service = new BookingService(theatre);
        Show show = theatre.getShows().get(0);

        try {
            Booking b = service.bookTickets(show, List.of("E5"), null);
            assertTrue(show.getSeat("E5").isBooked(), "E5 should be booked");

            service.cancelBooking(b.getBookingId());
            assertTrue(!show.getSeat("E5").isBooked(), "E5 should be released after cancellation");

            // Re-book E5
            Booking b2 = service.bookTickets(show, List.of("E5"), null);
            assertTrue(b2 != null, "Re-booking E5 must succeed");
            assertTrue(show.getSeat("E5").isBooked(), "E5 is booked again");
        } catch (BookingException e) {
            throw new AssertionError("Re-booking test failed: " + e.getMessage());
        }
    }
}

