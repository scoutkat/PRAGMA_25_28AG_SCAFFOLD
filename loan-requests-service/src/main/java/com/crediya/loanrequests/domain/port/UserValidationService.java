package com.crediya.loanrequests.domain.port;

import reactor.core.publisher.Mono;

/**
 * Service port interface for user validation operations
 * This interface defines the contract for validating users from external services
 * Following hexagonal architecture principles - this is a port in the domain layer
 */
public interface UserValidationService {
    
    /**
     * Validates if a user exists in the authentication service
     * @param userEmail the user email to validate
     * @return Mono containing true if user exists, false otherwise
     */
    Mono<Boolean> validateUserExists(String userEmail);
    
    /**
     * Gets user information from the authentication service
     * @param userEmail the user email to get information for
     * @return Mono containing user information if found, empty if not found
     */
    Mono<UserInfo> getUserInfo(String userEmail);
    
    /**
     * User information DTO for inter-service communication
     */
    record UserInfo(
            Long id,
            String firstName,
            String lastName,
            String email,
            java.math.BigDecimal baseSalary,
            Boolean isActive
    ) {}
}