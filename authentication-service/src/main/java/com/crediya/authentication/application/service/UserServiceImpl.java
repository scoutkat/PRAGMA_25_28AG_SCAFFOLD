package com.crediya.authentication.application.service;

import com.crediya.authentication.domain.exception.UserAlreadyExistsException;
import com.crediya.authentication.domain.model.User;
import com.crediya.authentication.domain.port.UserRepository;
import com.crediya.authentication.domain.port.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of UserService following hexagonal architecture
 * This class contains the business logic for user management
 * Uses reactive programming with WebFlux
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public Mono<User> registerUser(User user) {
        logger.info("Starting user registration process for email: {}", user.getEmail());
        
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        logger.warn("User registration failed - email already exists: {}", user.getEmail());
                        return Mono.error(new UserAlreadyExistsException(user.getEmail()));
                    }
                    
                    logger.info("Email validation passed, proceeding with user registration: {}", user.getEmail());
                    return userRepository.save(user);
                })
                .doOnSuccess(savedUser -> logger.info("User registered successfully with ID: {}", savedUser.getId()))
                .doOnError(error -> logger.error("User registration failed for email: {}, error: {}", 
                        user.getEmail(), error.getMessage()));
    }
    
    @Override
    public Mono<User> findUserByEmail(String email) {
        logger.debug("Searching for user by email: {}", email);
        
        return userRepository.findByEmail(email)
                .doOnSuccess(user -> {
                    if (user != null) {
                        logger.debug("User found by email: {}", email);
                    } else {
                        logger.debug("No user found with email: {}", email);
                    }
                })
                .doOnError(error -> logger.error("Error searching user by email: {}, error: {}", 
                        email, error.getMessage()));
    }
    
    @Override
    public Mono<User> findUserById(Long id) {
        logger.debug("Searching for user by ID: {}", id);
        
        return userRepository.findById(id)
                .doOnSuccess(user -> {
                    if (user != null) {
                        logger.debug("User found by ID: {}", id);
                    } else {
                        logger.debug("No user found with ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error searching user by ID: {}, error: {}", 
                        id, error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> validateUserExists(String email) {
        logger.debug("Validating user existence for email: {}", email);
        
        return userRepository.existsByEmail(email)
                .doOnSuccess(exists -> logger.debug("User existence validation result for {}: {}", 
                        email, exists))
                .doOnError(error -> logger.error("Error validating user existence for email: {}, error: {}", 
                        email, error.getMessage()));
    }
    
    @Override
    public Mono<User> updateUser(User user) {
        logger.info("Starting user update process for ID: {}", user.getId());
        
        return userRepository.update(user)
                .doOnSuccess(updatedUser -> logger.info("User updated successfully: {}", updatedUser.getId()))
                .doOnError(error -> logger.error("User update failed for ID: {}, error: {}", 
                        user.getId(), error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> deactivateUser(Long id) {
        logger.info("Starting user deactivation process for ID: {}", id);
        
        return userRepository.findById(id)
                .flatMap(user -> {
                    user.deactivate();
                    return userRepository.update(user);
                })
                .then(Mono.just(true))
                .doOnSuccess(result -> logger.info("User deactivated successfully: {}", id))
                .doOnError(error -> logger.error("User deactivation failed for ID: {}, error: {}", 
                        id, error.getMessage()));
    }
}
