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
    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
        
        // Create test user
        testUser = new User(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St, City, Country",
                "+1234567890",
                "john.doe@example.com",
                new BigDecimal("50000")
        );
    }

    @Test
    @DisplayName("Should register user successfully when email does not exist")
    void shouldRegisterUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));

        // When & Then
        StepVerifier.create(userService.registerUser(testUser))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(userService.registerUser(testUser))
                .expectError(UserAlreadyExistsException.class)
                .verify();
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmail() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.just(testUser));

        // When & Then
        StepVerifier.create(userService.findUserByEmail(testUser.getEmail()))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void shouldFindUserById() {
        // Given
        Long userId = 1L;
        testUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));

        // When & Then
        StepVerifier.create(userService.findUserById(userId))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate user exists returns true when user exists")
    void shouldValidateUserExistsReturnsTrue() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(userService.validateUserExists(testUser.getEmail()))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate user exists returns false when user does not exist")
    void shouldValidateUserExistsReturnsFalse() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(userService.validateUserExists("nonexistent@example.com"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        testUser.setId(1L);
        when(userRepository.update(any(User.class))).thenReturn(Mono.just(testUser));

        // When & Then
        StepVerifier.create(userService.updateUser(testUser))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should deactivate user successfully")
    void shouldDeactivateUserSuccessfully() {
        // Given
        Long userId = 1L;
        testUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));
        when(userRepository.update(any(User.class))).thenReturn(Mono.just(testUser));

        // When & Then
        StepVerifier.create(userService.deactivateUser(userId))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle repository errors gracefully")
    void shouldHandleRepositoryErrors() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(userService.registerUser(testUser))
                .expectError(RuntimeException.class)
                .verify();
    }
}