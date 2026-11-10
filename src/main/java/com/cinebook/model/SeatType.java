package com.cinebook.model;

/**
 * Enum representing the types of cinema seats and their respective prices.
 * Demonstrates: Enums with fields, constructors, and encapsulation.
 */
public enum SeatType {
    REGULAR(150.0),
    PREMIUM(250.0),
    VIP(400.0);

    private final double price;

    /**
     * Constructor initializing the price for each seat type.
     *
     * @param price the ticket price in Indian Rupees (INR)
     */
    SeatType(double price) {
        this.price = price;
    }

    /**
     * Returns the base price of the seat type.
     *
     * @return seat price
     */
    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}

