package com.crediya.authentication.domain.exception;

/**
 * Custom exception thrown when attempting to register a user with an email that already exists
 * This follows the principle of using specific exceptions for better error handling
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    private final String email;

    public UserAlreadyExistsException(String email) {
        super("User with email " + email + " already exists");
        this.email = email;
    }

    public UserAlreadyExistsException(String email, String message) {
        super(message);
        this.email = email;
    }

    public UserAlreadyExistsException(String email, String message, Throwable cause) {
        super(message, cause);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
