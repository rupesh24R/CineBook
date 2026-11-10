package com.cinebook.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CancellationResponseDto {
    private String bookingId;
    private BigDecimal originalAmount;
    private BigDecimal refundAmount; // 80% of paid amount
    private String bookingStatus;
    private LocalDateTime cancelledAt;
    private List<String> releasedSeats;
    private String message;

    public CancellationResponseDto() {
    }

    public CancellationResponseDto(String bookingId, BigDecimal originalAmount, BigDecimal refundAmount,
                                   String bookingStatus, LocalDateTime cancelledAt, List<String> releasedSeats, String message) {
        this.bookingId = bookingId;
        this.originalAmount = originalAmount;
        this.refundAmount = refundAmount;
        this.bookingStatus = bookingStatus;
        this.cancelledAt = cancelledAt;
        this.releasedSeats = releasedSeats;
        this.message = message;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public List<String> getReleasedSeats() {
        return releasedSeats;
    }

    public void setReleasedSeats(List<String> releasedSeats) {
        this.releasedSeats = releasedSeats;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

