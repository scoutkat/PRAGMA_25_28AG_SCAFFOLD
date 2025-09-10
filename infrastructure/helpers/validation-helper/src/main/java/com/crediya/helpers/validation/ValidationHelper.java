package com.crediya.helpers.validation;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Helper class for common validation operations
 * Provides reusable validation methods across the application
 * Following the principle of DRY (Don't Repeat Yourself)
 */
public class ValidationHelper {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Phone validation pattern (international format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{1,14}$"
    );
    
    // Colombian ID validation pattern (Cédula)
    private static final Pattern COLOMBIAN_ID_PATTERN = Pattern.compile(
            "^[0-9]{6,10}$"
    );
    
    /**
     * Validates if an email address has a correct format
     * @param email the email to validate
     * @return true if email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validates if a phone number has a correct international format
     * @param phone the phone number to validate
     * @return true if phone format is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Validates if a Colombian ID (Cédula) has a correct format
     * @param id the ID to validate
     * @return true if ID format is valid, false otherwise
     */
    public static boolean isValidColombianId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return COLOMBIAN_ID_PATTERN.matcher(id.trim()).matches();
    }
    
    /**
     * Validates if a string is not null and not empty
     * @param value the string to validate
     * @return true if string is valid, false otherwise
     */
    public static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates if a BigDecimal is within a specified range
     * @param value the value to validate
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @return true if value is within range, false otherwise
     */
    public static boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null || min == null || max == null) {
            return false;
        }
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }
    
    /**
     * Validates if an integer is within a specified range
     * @param value the value to validate
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @return true if value is within range, false otherwise
     */
    public static boolean isInRange(Integer value, Integer min, Integer max) {
        if (value == null || min == null || max == null) {
            return false;
        }
        return value >= min && value <= max;
    }
    
    /**
     * Validates if a string length is within specified bounds
     * @param value the string to validate
     * @param minLength the minimum allowed length
     * @param maxLength the maximum allowed length
     * @return true if length is within bounds, false otherwise
     */
    public static boolean isLengthValid(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Sanitizes a string by trimming whitespace and converting to lowercase
     * @param value the string to sanitize
     * @return sanitized string or null if input is null
     */
    public static String sanitizeString(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase();
    }
    
    /**
     * Validates if a string contains only letters and spaces
     * @param value the string to validate
     * @return true if string contains only letters and spaces, false otherwise
     */
    public static boolean containsOnlyLettersAndSpaces(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return value.trim().matches("^[a-zA-Z\\s]+$");
    }
    
    /**
     * Validates if a string contains only alphanumeric characters
     * @param value the string to validate
     * @return true if string contains only alphanumeric characters, false otherwise
     */
    public static boolean containsOnlyAlphanumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return value.trim().matches("^[a-zA-Z0-9]+$");
    }
}
