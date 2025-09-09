package com.crediya.loanrequests.domain.exception;

/**
 * Exception thrown when a loan request contains invalid data
 * This exception is used for business rule violations in loan requests
 */
public class InvalidLoanRequestException extends RuntimeException {
    
    public InvalidLoanRequestException(String message) {
        super(message);
    }
    
    public InvalidLoanRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}