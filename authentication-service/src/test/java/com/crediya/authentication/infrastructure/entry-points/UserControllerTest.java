package com.crediya.authentication.infrastructure.entrypoints;

import com.crediya.authentication.domain.exception.UserAlreadyExistsException;
import com.crediya.authentication.domain.model.User;
import com.crediya.authentication.domain.port.UserService;
import com.crediya.authentication.infrastructure.dto.ApiResponse;
import com.crediya.authentication.infrastructure.dto.UserRegistrationRequest;
import com.crediya.authentication.infrastructure.dto.UserResponse;
import com.crediya.authentication.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserController
 * Tests the REST API layer with mocked service dependencies
 * Uses reactive testing with StepVerifier for WebFlux testing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    private UserController userController;
    private User testUser;
    private UserRegistrationRequest testRequest;
    private UserResponse testResponse;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService, userMapper);
        
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
        testUser.setId(1L);

        // Create test request
        testRequest = new UserRegistrationRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St, City, Country",
                "+1234567890",
                "john.doe@example.com",
                new BigDecimal("50000")
        );

        // Create test response
        testResponse = new UserResponse();
        testResponse.setId(1L);
        testResponse.setFirstName("John");
        testResponse.setLastName("Doe");
        testResponse.setEmail("john.doe@example.com");
        testResponse.setIsActive(true);
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        // Given
        when(userMapper.toDomain(testRequest)).thenReturn(testUser);
        when(userService.registerUser(testUser)).thenReturn(Mono.just(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testResponse);

        // When
        Mono<ResponseEntity<ApiResponse<UserResponse>>> result = userController.registerUser(testRequest);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    assertTrue(response.getBody().isSuccess());
                    assertEquals("User registered successfully", response.getBody().getMessage());
                    assertNotNull(response.getBody().getData());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return conflict when user already exists")
    void shouldReturnConflictWhenUserExists() {
        // Given
        when(userMapper.toDomain(testRequest)).thenReturn(testUser);
        when(userService.registerUser(testUser))
                .thenReturn(Mono.error(new UserAlreadyExistsException(testUser.getEmail())));

        // When
        Mono<ResponseEntity<ApiResponse<UserResponse>>> result = userController.registerUser(testRequest);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
                    assertFalse(response.getBody().isSuccess());
                    assertTrue(response.getBody().getMessage().contains("already exists"));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return bad request for validation errors")
    void shouldReturnBadRequestForValidationErrors() {
        // Given
        when(userMapper.toDomain(testRequest)).thenReturn(testUser);
        when(userService.registerUser(testUser))
                .thenReturn(Mono.error(new IllegalArgumentException("Validation error")));

        // When
        Mono<ResponseEntity<ApiResponse<UserResponse>>> result = userController.registerUser(testRequest);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertFalse(response.getBody().isSuccess());
                    assertTrue(response.getBody().getMessage().contains("Validation error"));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate user exists successfully")
    void shouldValidateUserExistsSuccessfully() {
        // Given
        String email = "john.doe@example.com";
        when(userService.validateUserExists(email)).thenReturn(Mono.just(true));

        // When
        Mono<ResponseEntity<ApiResponse<Boolean>>> result = userController.validateUserExists(email);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertTrue(response.getBody().isSuccess());
                    assertEquals("User exists", response.getBody().getMessage());
                    assertTrue(response.getBody().getData());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return false when user does not exist")
    void shouldReturnFalseWhenUserDoesNotExist() {
        // Given
        String email = "nonexistent@example.com";
        when(userService.validateUserExists(email)).thenReturn(Mono.just(false));

        // When
        Mono<ResponseEntity<ApiResponse<Boolean>>> result = userController.validateUserExists(email);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertTrue(response.getBody().isSuccess());
                    assertEquals("User does not exist", response.getBody().getMessage());
                    assertFalse(response.getBody().getData());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void shouldGetUserByEmailSuccessfully() {
        // Given
        String email = "john.doe@example.com";
        when(userService.findUserByEmail(email)).thenReturn(Mono.just(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testResponse);

        // When
        Mono<ResponseEntity<ApiResponse<UserResponse>>> result = userController.getUserByEmail(email);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertTrue(response.getBody().isSuccess());
                    assertEquals("User found successfully", response.getBody().getMessage());
                    assertNotNull(response.getBody().getData());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return not found when user does not exist")
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        // Given
        String email = "nonexistent@example.com";
        when(userService.findUserByEmail(email)).thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<ApiResponse<UserResponse>>> result = userController.getUserByEmail(email);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    assertFalse(response.getBody().isSuccess());
                    assertTrue(response.getBody().getMessage().contains("not found"));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle internal server errors gracefully")
    void shouldHandleInternalServerErrors() {
        // Given
        when(userMapper.toDomain(testRequest)).thenReturn(testUser);
        when(userService.registerUser(testUser))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        // When
        Mono<ResponseEntity<ApiResponse<UserResponse>>> result = userController.registerUser(testRequest);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertFalse(response.getBody().isSuccess());
                    assertTrue(response.getBody().getMessage().contains("unexpected error"));
                })
                .verifyComplete();
    }
}
