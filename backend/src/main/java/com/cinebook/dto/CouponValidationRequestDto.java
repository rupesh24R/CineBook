package com.cinebook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CouponValidationRequestDto {

    @NotBlank(message = "Coupon code cannot be empty")
    private String code;

    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    public CouponValidationRequestDto() {
    }

    public CouponValidationRequestDto(String code, BigDecimal totalAmount) {
        this.code = code;
        this.totalAmount = totalAmount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}

