package com.crediya.authentication.domain.exception;

/**
 * Custom exception thrown when a user is not found in the system
 * This follows the principle of using specific exceptions for better error handling
 */
public class UserNotFoundException extends RuntimeException {
    
    private final String identifier;

    public UserNotFoundException(String identifier) {
        super("User not found with identifier: " + identifier);
        this.identifier = identifier;
    }

    public UserNotFoundException(String identifier, String message) {
        super(message);
        this.identifier = identifier;
    }

    public UserNotFoundException(String identifier, String message, Throwable cause) {
        super(message, cause);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
