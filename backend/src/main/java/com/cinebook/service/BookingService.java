package com.cinebook.service;

import com.cinebook.dto.BookingRequestDto;
import com.cinebook.dto.BookingResponseDto;
import com.cinebook.dto.CancellationResponseDto;
import com.cinebook.dto.CouponValidationResponseDto;
import com.cinebook.entity.Booking;
import com.cinebook.entity.BookingSeat;
import com.cinebook.entity.Seat;
import com.cinebook.entity.Show;
import com.cinebook.exception.BookingException;
import com.cinebook.exception.ResourceNotFoundException;
import com.cinebook.repository.BookingRepository;
import com.cinebook.repository.SeatRepository;
import com.cinebook.repository.ShowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Pattern SEAT_PATTERN = Pattern.compile("^[A-E][1-8]$");
    private static final BigDecimal REFUND_PERCENTAGE = new BigDecimal("0.80");

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final CouponService couponService;
    private final SeatService seatService;

    public BookingService(BookingRepository bookingRepository,
                          ShowRepository showRepository,
                          SeatRepository seatRepository,
                          CouponService couponService,
                          SeatService seatService) {
        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.couponService = couponService;
        this.seatService = seatService;
    }

    /**
     * Atomically reserves multiple seats using pessimistic locking and database transactions.
     * Prevents race conditions and double-booking.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BookingResponseDto createBooking(BookingRequestDto request) {
        if (request.getShowId() == null) {
            throw new BookingException("Show ID is required.");
        }
        if (request.getSeatCodes() == null || request.getSeatCodes().isEmpty()) {
            throw new BookingException("Please select at least one seat.");
        }

        // 1. Normalize and clean seat codes
        List<String> normalizedCodes = new ArrayList<>();
        Set<String> duplicateCheck = new HashSet<>();
        for (String raw : request.getSeatCodes()) {
            if (raw == null || raw.trim().isEmpty()) {
                throw new BookingException("Empty seat code in selection.");
            }
            String upper = raw.trim().toUpperCase();
            if (!SEAT_PATTERN.matcher(upper).matches()) {
                throw new BookingException("Invalid seat code: '" + upper + "'. Valid seats range from A1 to E8.");
            }
            if (!duplicateCheck.add(upper)) {
                throw new BookingException("Duplicate seat selection detected: " + upper);
            }
            normalizedCodes.add(upper);
        }

        // 2. Retrieve show
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + request.getShowId()));

        if (!Boolean.TRUE.equals(show.getActive())) {
            throw new BookingException("This show is no longer active for booking.");
        }

        // Ensure seats exist for this show
        List<Seat> currentSeats = seatRepository.findByShowIdOrderBySeatCodeAsc(show.getId());
        if (currentSeats.isEmpty()) {
            seatService.initializeSeatsForShow(show);
        }

        // 3. Acquire pessimistic lock on the requested seats (concurrency protection)
        List<Seat> lockedSeats = seatRepository.findByShowIdAndSeatCodeInWithLock(show.getId(), normalizedCodes);

        if (lockedSeats.size() != normalizedCodes.size()) {
            throw new BookingException("One or more selected seats do not exist in this auditorium.");
        }

        // 4. Validate seat availability (atomic check)
        List<String> alreadyBooked = new ArrayList<>();
        BigDecimal originalSubtotal = BigDecimal.ZERO;
        List<String> categories = new ArrayList<>();

        for (Seat seat : lockedSeats) {
            if (Boolean.TRUE.equals(seat.getIsBooked())) {
                alreadyBooked.add(seat.getSeatCode());
            }
            // Strict server-side pricing from SeatType enum (never trust client)
            BigDecimal seatPrice = seat.getSeatType().getPrice();
            originalSubtotal = originalSubtotal.add(seatPrice);

            String cat = seat.getSeatType().name();
            if (!categories.contains(cat)) {
                categories.add(cat);
            }
        }

        if (!alreadyBooked.isEmpty()) {
            throw new BookingException("The following seat(s) are already booked: " + String.join(", ", alreadyBooked));
        }

        // 5. Apply coupon if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalTotal = originalSubtotal;

        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            CouponValidationResponseDto couponResult = couponService.validateCoupon(request.getCouponCode(), originalSubtotal);
            if (couponResult.isValid()) {
                discountAmount = couponResult.getDiscountAmount();
                finalTotal = couponResult.getFinalAmount();
            }
        }

        // 6. Generate sequential unique booking identifier
        String bookingId = generateUniqueBookingId();

        // 7. Mark all seats as booked
        for (Seat seat : lockedSeats) {
            seat.setIsBooked(true);
        }
        seatRepository.saveAll(lockedSeats);

        // 8. Create and persist Booking entity
        Booking booking = new Booking(bookingId, show, finalTotal, discountAmount);
        Booking savedBooking = bookingRepository.save(booking);

        // 9. Persist BookingSeat mappings
        List<BookingSeat> bookingSeats = new ArrayList<>();
        for (Seat seat : lockedSeats) {
            bookingSeats.add(new BookingSeat(savedBooking, seat, seat.getSeatType().getPrice()));
        }
        savedBooking.setBookingSeats(bookingSeats);
        bookingRepository.save(savedBooking);

        return mapToDto(savedBooking, originalSubtotal, String.join(", ", categories), normalizedCodes);
    }

    /**
     * Cancels an existing booking and computes an 80% refund on the amount actually paid.
     * Releases seats immediately and prevents repeated cancellations.
     */
    @Transactional
    public CancellationResponseDto cancelBooking(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new BookingException("Booking ID is required for cancellation.");
        }

        Booking booking = bookingRepository.findByBookingId(bookingId.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No booking found with ID: " + bookingId.trim().toUpperCase()));

        if ("CANCELLED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new BookingException("Booking " + booking.getBookingId() + " has already been cancelled.");
        }

        // Release all associated seats
        List<String> releasedSeatCodes = new ArrayList<>();
        if (booking.getBookingSeats() != null) {
            for (BookingSeat bs : booking.getBookingSeats()) {
                Seat seat = bs.getSeat();
                if (seat != null) {
                    seat.setIsBooked(false);
                    seatRepository.save(seat);
                    releasedSeatCodes.add(seat.getSeatCode());
                }
            }
        }

        // Calculate 80% refund based on actual amount paid
        BigDecimal refundAmount = booking.getTotalAmount()
                .multiply(REFUND_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);

        // Update booking status
        booking.setBookingStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now());
        bookingRepository.save(booking);

        return new CancellationResponseDto(
                booking.getBookingId(),
                booking.getTotalAmount(),
                refundAmount,
                "CANCELLED",
                booking.getCancelledAt(),
                releasedSeatCodes,
                String.format("Booking %s cancelled successfully. 80%% refund of Rs. %s has been initiated.",
                        booking.getBookingId(), refundAmount.toPlainString())
        );
    }

    @Transactional(readOnly = true)
    public BookingResponseDto getBookingByBookingId(String bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        return mapToDto(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private synchronized String generateUniqueBookingId() {
        long count = bookingRepository.count();
        long candidateNum = 1001 + count;
        String candidate = "BK" + candidateNum;

        // Ensure collision safety
        while (bookingRepository.existsByBookingId(candidate)) {
            candidateNum++;
            candidate = "BK" + candidateNum;
        }
        return candidate;
    }

    private BookingResponseDto mapToDto(Booking booking) {
        List<String> seatCodes = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        BigDecimal originalSubtotal = BigDecimal.ZERO;

        if (booking.getBookingSeats() != null) {
            for (BookingSeat bs : booking.getBookingSeats()) {
                if (bs.getSeat() != null) {
                    seatCodes.add(bs.getSeat().getSeatCode());
                    String cat = bs.getSeat().getSeatType().name();
                    if (!categories.contains(cat)) {
                        categories.add(cat);
                    }
                }
                originalSubtotal = originalSubtotal.add(bs.getPrice());
            }
        }

        return mapToDto(booking, originalSubtotal, String.join(", ", categories), seatCodes);
    }

    private BookingResponseDto mapToDto(Booking booking, BigDecimal originalAmount, String categories, List<String> seatCodes) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setBookingId(booking.getBookingId());
        dto.setShowId(booking.getShow().getId());
        dto.setMovieTitle(booking.getShow().getMovie().getTitle());
        dto.setMoviePosterUrl(booking.getShow().getMovie().getPosterUrl());
        dto.setShowDate(booking.getShow().getShowDate());
        dto.setShowTime(booking.getShow().getShowTime());
        dto.setSeatCodes(seatCodes);
        dto.setSeatCategories(categories);
        dto.setOriginalAmount(originalAmount);
        dto.setDiscountAmount(booking.getDiscountAmount());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setBookingStatus(booking.getBookingStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setCancelledAt(booking.getCancelledAt());
        return dto;
    }
}

