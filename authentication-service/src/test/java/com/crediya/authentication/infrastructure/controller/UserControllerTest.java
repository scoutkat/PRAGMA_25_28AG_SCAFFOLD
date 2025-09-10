package com.crediya.authentication.infrastructure.controller;

import com.crediya.authentication.domain.exception.UserAlreadyExistsException;
import com.crediya.authentication.domain.model.User;
import com.crediya.authentication.domain.port.UserService;
import com.crediya.authentication.infrastructure.dto.UserRegistrationRequest;
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
    
    @BeforeEach
    void setUp() {
        userController = new UserController(userService, userMapper);
    }
    
    @Test
    @DisplayName("Should register user successfully and return 201 status")
    void shouldRegisterUserSuccessfully() {
        // Given
        UserRegistrationRequest request = createTestRegistrationRequest();
        User user = createTestUser();
        user.setId(1L);
        
        when(userMapper.toDomain(any(UserRegistrationRequest.class))).thenReturn(user);
        when(userService.registerUser(any(User.class))).thenReturn(Mono.just(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(createTestUserResponse());
        
        // When & Then
        StepVerifier.create(userController.registerUser(request))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.CREATED &&
                        response.getBody() != null &&
                        response.getBody().isSuccess())
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should return 409 status when user already exists")
    void shouldReturn409WhenUserAlreadyExists() {
        // Given
        UserRegistrationRequest request = createTestRegistrationRequest();
        User user = createTestUser();
        
        when(userMapper.toDomain(any(UserRegistrationRequest.class))).thenReturn(user);
        when(userService.registerUser(any(User.class)))
                .thenReturn(Mono.error(new UserAlreadyExistsException("test@example.com")));
        
        // When & Then
        StepVerifier.create(userController.registerUser(request))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.CONFLICT &&
                        response.getBody() != null &&
                        !response.getBody().isSuccess())
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should validate user exists and return 200 status")
    void shouldValidateUserExists() {
        // Given
        String email = "test@example.com";
        
        when(userService.validateUserExists(anyString())).thenReturn(Mono.just(true));
        
        // When & Then
        StepVerifier.create(userController.validateUserExists(email))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.OK &&
                        response.getBody() != null &&
                        response.getBody().isSuccess() &&
                        response.getBody().getData() == true)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should get user by email and return 200 status")
    void shouldGetUserByEmail() {
        // Given
        String email = "test@example.com";
        User user = createTestUser();
        user.setId(1L);
        
        when(userService.findUserByEmail(anyString())).thenReturn(Mono.just(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(createTestUserResponse());
        
        // When & Then
        StepVerifier.create(userController.getUserByEmail(email))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.OK &&
                        response.getBody() != null &&
                        response.getBody().isSuccess())
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should return 404 when user not found")
    void shouldReturn404WhenUserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        
        when(userService.findUserByEmail(anyString())).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(userController.getUserByEmail(email))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.NOT_FOUND &&
                        response.getBody() != null &&
                        !response.getBody().isSuccess())
                .verifyComplete();
    }
    
    /**
     * Helper method to create a test registration request
     * @return UserRegistrationRequest for testing
     */
    private UserRegistrationRequest createTestRegistrationRequest() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setAddress("123 Main St, City, Country");
        request.setPhone("+1234567890");
        request.setEmail("test@example.com");
        request.setBaseSalary(new BigDecimal("50000.00"));
        return request;
    }
    
    /**
     * Helper method to create a test user
     * @return User instance for testing
     */
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setAddress("123 Main St, City, Country");
        user.setPhone("+1234567890");
        user.setEmail("test@example.com");
        user.setBaseSalary(new BigDecimal("50000.00"));
        return user;
    }
    
    /**
     * Helper method to create a test user response
     * @return UserResponse for testing
     */
    private com.crediya.authentication.infrastructure.dto.UserResponse createTestUserResponse() {
        com.crediya.authentication.infrastructure.dto.UserResponse response = 
                new com.crediya.authentication.infrastructure.dto.UserResponse();
        response.setId(1L);
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setFullName("John Doe");
        response.setEmail("test@example.com");
        response.setBaseSalary(new BigDecimal("50000.00"));
        response.setIsActive(true);
        return response;
    }
}
