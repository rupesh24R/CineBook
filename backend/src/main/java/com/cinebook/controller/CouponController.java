package com.cinebook.controller;

import com.cinebook.dto.CouponValidationRequestDto;
import com.cinebook.dto.CouponValidationResponseDto;
import com.cinebook.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/validate")
    public ResponseEntity<CouponValidationResponseDto> validateCoupon(@Valid @RequestBody CouponValidationRequestDto request) {
        return ResponseEntity.ok(couponService.validateCoupon(request.getCode(), request.getTotalAmount()));
    }
}

