package com.cinebook.dto;

import java.math.BigDecimal;

public class SeatResponseDto {
    private Long id;
    private Long showId;
    private String seatCode;
    private String seatType;
    private BigDecimal price;
    private Boolean isBooked;

    public SeatResponseDto() {
    }

    public SeatResponseDto(Long id, Long showId, String seatCode, String seatType, BigDecimal price, Boolean isBooked) {
        this.id = id;
        this.showId = showId;
        this.seatCode = seatCode;
        this.seatType = seatType;
        this.price = price;
        this.isBooked = isBooked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getIsBooked() {
        return isBooked;
    }

    public void setIsBooked(Boolean booked) {
        isBooked = booked;
    }
}

