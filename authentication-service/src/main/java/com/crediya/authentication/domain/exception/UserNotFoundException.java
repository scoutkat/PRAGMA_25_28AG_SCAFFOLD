package com.crediya.authentication.domain.exception;

/**
 * Custom exception thrown when attempting to find a user that does not exist
 * This follows the principle of using specific exceptions for better error handling
 */
public class UserNotFoundException extends RuntimeException {
    
    private final String identifier;
    private final String identifierType;

    public UserNotFoundException(String identifier, String identifierType) {
        super("User not found with " + identifierType + ": " + identifier);
        this.identifier = identifier;
        this.identifierType = identifierType;
    }

    public UserNotFoundException(String identifier, String identifierType, String message) {
        super(message);
        this.identifier = identifier;
        this.identifierType = identifierType;
    }

    public UserNotFoundException(String identifier, String identifierType, String message, Throwable cause) {
        super(message, cause);
        this.identifier = identifier;
        this.identifierType = identifierType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getIdentifierType() {
        return identifierType;
    }
}