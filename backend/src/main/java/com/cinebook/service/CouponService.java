package com.cinebook.service;

import com.cinebook.dto.CouponValidationResponseDto;
import com.cinebook.entity.Coupon;
import com.cinebook.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponValidationResponseDto validateCoupon(String code, BigDecimal totalAmount) {
        if (code == null || code.trim().isEmpty()) {
            return new CouponValidationResponseDto("", false, BigDecimal.ZERO, BigDecimal.ZERO, totalAmount, "Coupon code cannot be empty.");
        }

        String cleanedCode = code.trim().toUpperCase();
        Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCaseAndActiveTrue(cleanedCode);

        if (couponOpt.isEmpty()) {
            return new CouponValidationResponseDto(cleanedCode, false, BigDecimal.ZERO, BigDecimal.ZERO, totalAmount, "Invalid or expired coupon code.");
        }

        Coupon coupon = couponOpt.get();
        BigDecimal percentage = coupon.getDiscountPercentage();

        BigDecimal safeTotal = (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0)
                ? totalAmount : BigDecimal.ZERO;

        BigDecimal discountAmount = safeTotal.multiply(percentage)
                .divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);

        BigDecimal finalAmount = safeTotal.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        return new CouponValidationResponseDto(
                coupon.getCode(),
                true,
                percentage,
                discountAmount,
                finalAmount,
                String.format("Coupon '%s' applied successfully! (%.0f%% off)", coupon.getCode(), percentage)
        );
    }
}

