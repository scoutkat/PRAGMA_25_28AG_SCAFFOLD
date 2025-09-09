package com.crediya.authentication.domain.port;

import com.crediya.authentication.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * Service port interface for User domain operations
 * This interface defines the business logic contract for user management
 * Following hexagonal architecture principles - this is a port in the domain layer
 */
public interface UserService {
    
    /**
     * Registers a new user in the system
     * Validates business rules and ensures data integrity
     * @param user the user entity to register
     * @return Mono containing the registered user with generated ID
     */
    Mono<User> registerUser(User user);
    
    /**
     * Finds a user by email address
     * @param email the email to search for
     * @return Mono containing the user if found, empty if not found
     */
    Mono<User> findUserByEmail(String email);
    
    /**
     * Finds a user by ID
     * @param id the user ID to search for
     * @return Mono containing the user if found, empty if not found
     */
    Mono<User> findUserById(Long id);
    
    /**
     * Validates if a user exists with the given email
     * Used by other microservices to validate user existence
     * @param email the email to validate
     * @return Mono containing true if user exists, false otherwise
     */
    Mono<Boolean> validateUserExists(String email);
    
    /**
     * Updates an existing user's information
     * @param user the user entity with updated information
     * @return Mono containing the updated user
     */
    Mono<User> updateUser(User user);
    
    /**
     * Deactivates a user account
     * @param id the user ID to deactivate
     * @return Mono containing true if deactivated successfully
     */
    Mono<Boolean> deactivateUser(Long id);
}
