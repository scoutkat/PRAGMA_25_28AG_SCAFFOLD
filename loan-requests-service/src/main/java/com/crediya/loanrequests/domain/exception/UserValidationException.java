package com.crediya.loanrequests.domain.exception;

/**
 * Exception thrown when user validation fails
 * This exception is used when the user validation service returns an error
 */
public class UserValidationException extends RuntimeException {
    
    public UserValidationException(String message) {
        super(message);
    }
    
    public UserValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}