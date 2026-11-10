package com.cinebook.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class providing robust validation and parsing for console user inputs.
 * Ensures the application handles unexpected, malformed, or out-of-range input gracefully.
 */
public class InputValidator {
    // Regular expression for individual seat codes: A-E followed by 1-8
    private static final Pattern SEAT_CODE_PATTERN = Pattern.compile("^[A-Ea-e][1-8]$");

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private InputValidator() {
    }

    /**
     * Checks if a string is null, empty, or contains only whitespace.
     *
     * @param input the input string
     * @return true if empty; false otherwise
     */
    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    /**
     * Parses an integer from string input.
     *
     * @param input string to parse
     * @return parsed Integer, or null if invalid format
     */
    public static Integer parseInteger(String input) {
        if (isEmpty(input)) {
            return null;
        }
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Validates whether a menu choice integer falls within the valid range [min, max].
     *
     * @param choice chosen option
     * @param min    minimum valid option
     * @param max    maximum valid option
     * @return true if within bounds; false otherwise
     */
    public static boolean isValidMenuChoice(Integer choice, int min, int max) {
        return choice != null && choice >= min && choice <= max;
    }

    /**
     * Validates whether an individual seat code follows the A1-E8 pattern.
     *
     * @param seatCode seat code to validate
     * @return true if valid format; false otherwise
     */
    public static boolean isValidSeatFormat(String seatCode) {
        if (isEmpty(seatCode)) {
            return false;
        }
        return SEAT_CODE_PATTERN.matcher(seatCode.trim()).matches();
    }

    /**
     * Parses a comma-separated seat selection string into a clean list of uppercase codes.
     * Trims spaces around tokens and ignores trailing commas.
     *
     * @param input comma-separated input (e.g., "C3, C4, E2")
     * @return list of normalized uppercase seat codes
     */
    public static List<String> parseSeatCodes(String input) {
        List<String> codes = new ArrayList<>();
        if (isEmpty(input)) {
            return codes;
        }

        String[] parts = input.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                codes.add(trimmed.toUpperCase());
            }
        }
        return codes;
    }

    /**
     * Checks if a coupon code format is syntactically valid (alphanumeric, 3-15 chars).
     *
     * @param coupon code
     * @return true if non-empty alphanumeric format
     */
    public static boolean isValidCouponFormat(String coupon) {
        if (isEmpty(coupon)) {
            return false;
        }
        return coupon.trim().matches("^[A-Za-z0-9]{3,15}$");
    }
}

