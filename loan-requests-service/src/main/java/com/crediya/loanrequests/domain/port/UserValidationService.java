package com.crediya.loanrequests.domain.port;

import reactor.core.publisher.Mono;

/**
 * Service port interface for user validation operations
 * This interface defines the contract for validating users with external services
 * Following hexagonal architecture principles - this is a port in the domain layer
 */
public interface UserValidationService {
    
    /**
     * Validates if a user exists with the given email
     * This method communicates with the Authentication service
     * @param email the email to validate
     * @return Mono containing true if user exists, false otherwise
     */
    Mono<Boolean> validateUserExists(String email);
}