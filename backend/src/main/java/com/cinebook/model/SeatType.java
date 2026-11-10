package com.cinebook.model;

import java.math.BigDecimal;

/**
 * Enum representing cinema seating tiers and their respective base prices.
 * Migrated from console architecture and upgraded to use BigDecimal for exact financial calculations.
 */
public enum SeatType {
    REGULAR(new BigDecimal("150.00")),
    PREMIUM(new BigDecimal("250.00")),
    VIP(new BigDecimal("400.00"));

    private final BigDecimal price;

    SeatType(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public static SeatType fromRow(char rowChar) {
        char upper = Character.toUpperCase(rowChar);
        if (upper == 'A' || upper == 'B') {
            return REGULAR;
        } else if (upper == 'C' || upper == 'D') {
            return PREMIUM;
        } else if (upper == 'E') {
            return VIP;
        }
        return REGULAR;
    }
}

