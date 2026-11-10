package com.cinebook;

import com.cinebook.model.Booking;
import com.cinebook.model.Seat;
import com.cinebook.model.SeatType;
import com.cinebook.model.Show;
import com.cinebook.service.BookingService;
import com.cinebook.service.Theatre;
import com.cinebook.util.BookingException;
import com.cinebook.util.InputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Unit Tests for CineBook.
 * Covers all functional requirements, edge cases, and business rules.
 */
public class CineBookJUnitTest {

    private Theatre theatre;
    private BookingService bookingService;
    private Show show;

    @BeforeEach
    public void setUp() {
        theatre = new Theatre();
        bookingService = new BookingService(theatre);
        show = theatre.getShows().get(0); // Interstellar (7:30 PM)
    }

    @Test
    @DisplayName("FR-1: Theatre should initialize with at least three movie shows")
    public void testShowListing() {
        List<Show> shows = theatre.getShows();
        assertNotNull(shows);
        assertTrue(shows.size() >= 3, "Theatre must have at least 3 shows.");
        assertEquals("Interstellar", shows.get(0).getMovieName());
        assertEquals("Inception", shows.get(1).getMovieName());
        assertEquals("The Dark Knight", shows.get(2).getMovieName());
    }

    @Test
    @DisplayName("FR-2 & FR-3: 5x8 Seat Grid initialization and correct category pricing")
    public void testSeatGridInitialization() {
        Seat[][] seats = show.getSeats();
        assertEquals(5, seats.length, "Grid should have 5 rows");
        assertEquals(8, seats[0].length, "Grid should have 8 columns");

        // Verify categories
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 8; c++) {
                Seat seat = seats[r][c];
                assertFalse(seat.isBooked(), "New seat must be unbooked");
                if (r < 2) {
                    assertEquals(SeatType.REGULAR, seat.getSeatType());
                    assertEquals(150.0, seat.getSeatType().getPrice(), 0.001);
                } else if (r < 4) {
                    assertEquals(SeatType.PREMIUM, seat.getSeatType());
                    assertEquals(250.0, seat.getSeatType().getPrice(), 0.001);
                } else {
                    assertEquals(SeatType.VIP, seat.getSeatType());
                    assertEquals(400.0, seat.getSeatType().getPrice(), 0.001);
                }
            }
        }
    }

    @Test
    @DisplayName("FR-4: Single and Multiple seat booking succeeds atomically")
    public void testMultipleSeatBooking() throws BookingException {
        Booking booking = bookingService.bookTickets(show, Arrays.asList("C3", "C4"), null);
        assertNotNull(booking);
        assertTrue(show.getSeat("C3").isBooked());
        assertTrue(show.getSeat("C4").isBooked());
        assertEquals(500.0, booking.getTotalAmount(), 0.001);
        assertEquals("BK1001", booking.getBookingId());
    }

    @Test
    @DisplayName("FR-4: Reject invalid seat codes (Z9, A0, F2)")
    public void testRejectInvalidSeatCodes() {
        assertThrows(BookingException.class, () -> bookingService.bookTickets(show, List.of("Z9"), null));
        assertThrows(BookingException.class, () -> bookingService.bookTickets(show, List.of("A0"), null));
        assertThrows(BookingException.class, () -> bookingService.bookTickets(show, List.of("F2"), null));
    }

    @Test
    @DisplayName("FR-4: Reject booking already occupied seat and atomic rollback")
    public void testRejectOccupiedSeat() throws BookingException {
        bookingService.bookTickets(show, List.of("A1"), null);
        assertTrue(show.getSeat("A1").isBooked());

        // Booking A1 again should fail
        assertThrows(BookingException.class, () -> bookingService.bookTickets(show, List.of("A1"), null));

        // Atomic check: attempting [A2, A1] fails and A2 must remain unbooked
        assertThrows(BookingException.class, () -> bookingService.bookTickets(show, Arrays.asList("A2", "A1"), null));
        assertFalse(show.getSeat("A2").isBooked(), "A2 must remain unbooked after atomic rollback");
    }

    @Test
    @DisplayName("FR-4: Reject duplicate seats in the same selection (C3, C3)")
    public void testRejectDuplicateSeats() {
        assertThrows(BookingException.class, () -> bookingService.bookTickets(show, Arrays.asList("C3", "C3"), null));
        assertFalse(show.getSeat("C3").isBooked());
    }

    @Test
    @DisplayName("FR-6: Promotional coupon CINE10 applies 10% discount")
    public void testCouponCine10() throws BookingException {
        Booking booking = bookingService.bookTickets(show, Arrays.asList("C3", "C4"), "CINE10");
        // 250 + 250 = 500; 10% off = 450
        assertEquals(450.0, booking.getTotalAmount(), 0.001);
    }

    @Test
    @DisplayName("FR-6: Invalid promotional coupon applies no discount")
    public void testInvalidCoupon() throws BookingException {
        Booking booking = bookingService.bookTickets(show, Arrays.asList("C3", "C4"), "INVALID123");
        assertEquals(500.0, booking.getTotalAmount(), 0.001);
    }

    @Test
    @DisplayName("FR-6: Method overloading for calculateTotal")
    public void testMethodOverloading() {
        String[] seats = new String[]{"A1", "C1", "E1"}; // 150 + 250 + 400 = 800
        assertEquals(800.0, show.calculateTotal(seats), 0.001);
        assertEquals(720.0, show.calculateTotal(seats, "CINE10"), 0.001);
        assertEquals(800.0, show.calculateTotal(seats, "NOT_REAL"), 0.001);
    }

    @Test
    @DisplayName("FR-7: Cancellation frees seats and computes 80% refund")
    public void testCancellationAndRefund() throws BookingException {
        Booking booking = bookingService.bookTickets(show, Arrays.asList("C3", "C4"), "CINE10");
        assertEquals(450.0, booking.getTotalAmount(), 0.001);

        Booking cancelled = bookingService.cancelBooking(booking.getBookingId());
        assertTrue(cancelled.isCancelled());
        assertEquals(360.0, cancelled.calculateRefundAmount(), 0.001);

        // Verify seats are released
        assertFalse(show.getSeat("C3").isBooked());
        assertFalse(show.getSeat("C4").isBooked());
    }

    @Test
    @DisplayName("FR-7: Duplicate cancellation is rejected")
    public void testDuplicateCancellationRejected() throws BookingException {
        Booking booking = bookingService.bookTickets(show, List.of("A1"), null);
        bookingService.cancelBooking(booking.getBookingId());

        assertThrows(BookingException.class, () -> bookingService.cancelBooking(booking.getBookingId()));
    }

    @Test
    @DisplayName("FR-7: Cancelling non-existent booking ID is rejected")
    public void testCancelNonExistentBooking() {
        assertThrows(BookingException.class, () -> bookingService.cancelBooking("BK9999"));
    }

    @Test
    @DisplayName("FR-8: Separate shows have completely independent seat maps")
    public void testIndependentSeatMaps() throws BookingException {
        Show show1 = theatre.getShows().get(0);
        Show show2 = theatre.getShows().get(1);

        bookingService.bookTickets(show1, List.of("A1"), null);
        assertTrue(show1.getSeat("A1").isBooked());
        assertFalse(show2.getSeat("A1").isBooked());

        // Booking same seat in show 2 must succeed
        Booking booking2 = bookingService.bookTickets(show2, List.of("A1"), null);
        assertNotNull(booking2);
        assertTrue(show2.getSeat("A1").isBooked());
    }

    @Test
    @DisplayName("InputValidator correctly processes comma-separated seat codes")
    public void testInputValidator() {
        List<String> parsed = InputValidator.parseSeatCodes(" a1 , B2,  e8 ");
        assertEquals(3, parsed.size());
        assertEquals("A1", parsed.get(0));
        assertEquals("B2", parsed.get(1));
        assertEquals("E8", parsed.get(2));

        assertTrue(InputValidator.isValidSeatFormat("A1"));
        assertFalse(InputValidator.isValidSeatFormat("Z9"));
        assertFalse(InputValidator.isValidSeatFormat("A0"));
    }
}

