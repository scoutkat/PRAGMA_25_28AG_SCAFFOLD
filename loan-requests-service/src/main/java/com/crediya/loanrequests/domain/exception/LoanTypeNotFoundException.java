package com.crediya.loanrequests.domain.exception;

/**
 * Exception thrown when a loan type is not found
 * This exception is used when trying to access a non-existent loan type
 */
public class LoanTypeNotFoundException extends RuntimeException {
    
    public LoanTypeNotFoundException(String message) {
        super(message);
    }
    
    public LoanTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}