package com.cinebook.util;

/**
 * Custom checked exception for handling domain-specific booking and cancellation errors.
 * Demonstrates: Robust Exception Handling in OOP.
 */
public class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}

