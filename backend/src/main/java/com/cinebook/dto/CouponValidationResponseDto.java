package com.cinebook.dto;

import java.math.BigDecimal;

public class CouponValidationResponseDto {
    private String code;
    private boolean valid;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String message;

    public CouponValidationResponseDto() {
    }

    public CouponValidationResponseDto(String code, boolean valid, BigDecimal discountPercentage,
                                       BigDecimal discountAmount, BigDecimal finalAmount, String message) {
        this.code = code;
        this.valid = valid;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

