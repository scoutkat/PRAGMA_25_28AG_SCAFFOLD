package com.crediya.authentication.application.service;

import com.crediya.authentication.domain.exception.UserAlreadyExistsException;
import com.crediya.authentication.domain.model.User;
import com.crediya.authentication.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserServiceImpl
 * Tests the business logic layer with mocked dependencies
 * Uses reactive testing with StepVerifier for WebFlux testing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Implementation Tests")
class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    private UserServiceImpl userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }
    
    @Test
    @DisplayName("Should register user successfully when email does not exist")
    void shouldRegisterUserSuccessfully() {
        // Given
        User user = createTestUser();
        User savedUser = createTestUser();
        savedUser.setId(1L);
        
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));
        
        // When & Then
        StepVerifier.create(userService.registerUser(user))
                .expectNext(savedUser)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        User user = createTestUser();
        
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(true));
        
        // When & Then
        StepVerifier.create(userService.registerUser(user))
                .expectError(UserAlreadyExistsException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmail() {
        // Given
        String email = "test@example.com";
        User user = createTestUser();
        user.setId(1L);
        
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));
        
        // When & Then
        StepVerifier.create(userService.findUserByEmail(email))
                .expectNext(user)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should find user by ID successfully")
    void shouldFindUserById() {
        // Given
        Long userId = 1L;
        User user = createTestUser();
        user.setId(userId);
        
        when(userRepository.findById(any(Long.class))).thenReturn(Mono.just(user));
        
        // When & Then
        StepVerifier.create(userService.findUserById(userId))
                .expectNext(user)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should validate user exists returns true")
    void shouldValidateUserExistsReturnsTrue() {
        // Given
        String email = "test@example.com";
        
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(true));
        
        // When & Then
        StepVerifier.create(userService.validateUserExists(email))
                .expectNext(true)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should validate user exists returns false")
    void shouldValidateUserExistsReturnsFalse() {
        // Given
        String email = "nonexistent@example.com";
        
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        
        // When & Then
        StepVerifier.create(userService.validateUserExists(email))
                .expectNext(false)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUser() {
        // Given
        User user = createTestUser();
        user.setId(1L);
        
        when(userRepository.update(any(User.class))).thenReturn(Mono.just(user));
        
        // When & Then
        StepVerifier.create(userService.updateUser(user))
                .expectNext(user)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should deactivate user successfully")
    void shouldDeactivateUser() {
        // Given
        Long userId = 1L;
        User user = createTestUser();
        user.setId(userId);
        
        when(userRepository.findById(any(Long.class))).thenReturn(Mono.just(user));
        when(userRepository.update(any(User.class))).thenReturn(Mono.just(user));
        
        // When & Then
        StepVerifier.create(userService.deactivateUser(userId))
                .expectNext(true)
                .verifyComplete();
    }
    
    /**
     * Helper method to create a test user
     * @return User instance for testing
     */
    private User createTestUser() {
        return new User(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St, City, Country",
                "+1234567890",
                "john.doe@example.com",
                new BigDecimal("50000.00")
        );
    }
}
