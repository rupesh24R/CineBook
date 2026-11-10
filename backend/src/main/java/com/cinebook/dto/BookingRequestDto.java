package com.cinebook.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BookingRequestDto {

    @NotNull(message = "showId is required")
    private Long showId;

    @NotEmpty(message = "At least one seat code must be selected")
    private List<String> seatCodes;

    private String couponCode;

    public BookingRequestDto() {
    }

    public BookingRequestDto(Long showId, List<String> seatCodes, String couponCode) {
        this.showId = showId;
        this.seatCodes = seatCodes;
        this.couponCode = couponCode;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public List<String> getSeatCodes() {
        return seatCodes;
    }

    public void setSeatCodes(List<String> seatCodes) {
        this.seatCodes = seatCodes;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}

