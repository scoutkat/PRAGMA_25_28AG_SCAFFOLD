package com.crediya.authentication.domain.port;

import com.crediya.authentication.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * Repository port interface for User domain operations
 * This interface defines the contract for user data access
 * Following hexagonal architecture principles - this is a port in the domain layer
 */
public interface UserRepository {
    
    /**
     * Saves a new user to the database
     * @param user the user entity to save
     * @return Mono containing the saved user with generated ID
     */
    Mono<User> save(User user);
    
    /**
     * Finds a user by email address
     * @param email the email to search for
     * @return Mono containing the user if found, empty if not found
     */
    Mono<User> findByEmail(String email);
    
    /**
     * Finds a user by ID
     * @param id the user ID to search for
     * @return Mono containing the user if found, empty if not found
     */
    Mono<User> findById(Long id);
    
    /**
     * Checks if a user exists with the given email
     * @param email the email to check
     * @return Mono containing true if user exists, false otherwise
     */
    Mono<Boolean> existsByEmail(String email);
    
    /**
     * Updates an existing user
     * @param user the user entity with updated information
     * @return Mono containing the updated user
     */
    Mono<User> update(User user);
    
    /**
     * Deletes a user by ID
     * @param id the user ID to delete
     * @return Mono containing true if deleted successfully, false otherwise
     */
    Mono<Boolean> deleteById(Long id);
}
