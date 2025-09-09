package com.crediya.authentication.infrastructure.controller;

import com.crediya.authentication.domain.exception.UserAlreadyExistsException;
import com.crediya.authentication.domain.exception.UserNotFoundException;
import com.crediya.authentication.domain.model.User;
import com.crediya.authentication.domain.port.UserService;
import com.crediya.authentication.infrastructure.dto.ApiResponse;
import com.crediya.authentication.infrastructure.dto.UserRegistrationRequest;
import com.crediya.authentication.infrastructure.dto.UserResponse;
import com.crediya.authentication.infrastructure.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST Controller for user management operations
 * Handles HTTP requests and responses using reactive programming with WebFlux
 * Provides endpoints for user registration and validation
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "User Management", description = "APIs for managing users in the CrediYa platform")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    private final UserMapper userMapper;
    
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }
    
    /**
     * Registers a new user in the system
     * Validates input data and handles business logic errors
     * 
     * @param request the user registration request
     * @return ResponseEntity containing the registered user information
     */
    @PostMapping("/usuarios")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided personal information. " +
                     "Validates that all required fields are provided and email is unique."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation errors",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "User with email already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        
        logger.info("Received user registration request for email: {}", request.getEmail());
        
        return userService.registerUser(userMapper.toDomain(request))
                .map(user -> {
                    UserResponse response = userMapper.toResponse(user);
                    ApiResponse<UserResponse> apiResponse = ApiResponse.success(
                            "User registered successfully", response);
                    
                    logger.info("User registration completed successfully for email: {}", request.getEmail());
                    return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
                })
                .onErrorResume(UserAlreadyExistsException.class, ex -> {
                    logger.warn("User registration failed - email already exists: {}", request.getEmail());
                    ApiResponse<UserResponse> errorResponse = ApiResponse.error(
                            "User with email " + request.getEmail() + " already exists");
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
                })
                .onErrorResume(IllegalArgumentException.class, ex -> {
                    logger.warn("User registration failed - validation error: {}", ex.getMessage());
                    ApiResponse<UserResponse> errorResponse = ApiResponse.error(
                            "Validation error: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("User registration failed with unexpected error for email: {}, error: {}", 
                            request.getEmail(), ex.getMessage(), ex);
                    ApiResponse<UserResponse> errorResponse = ApiResponse.error(
                            "An unexpected error occurred while registering the user");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }
    
    /**
     * Validates if a user exists by email
     * Used by other microservices to validate user existence
     * 
     * @param email the email to validate
     * @return ResponseEntity containing validation result
     */
    @GetMapping("/usuarios/validate")
    @Operation(
        summary = "Validate user existence",
        description = "Checks if a user exists with the provided email address. " +
                     "Used by other microservices for user validation."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User validation completed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid email format",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public Mono<ResponseEntity<ApiResponse<Boolean>>> validateUserExists(
            @Parameter(description = "Email address to validate", required = true)
            @RequestParam String email) {
        
        logger.info("Received user validation request for email: {}", email);
        
        return userService.validateUserExists(email)
                .map(exists -> {
                    String message = exists ? "User exists" : "User does not exist";
                    ApiResponse<Boolean> apiResponse = ApiResponse.success(message, exists);
                    
                    logger.info("User validation completed for email: {}, exists: {}", email, exists);
                    return ResponseEntity.ok(apiResponse);
                })
                .onErrorResume(IllegalArgumentException.class, ex -> {
                    logger.warn("User validation failed - invalid email format: {}", email);
                    ApiResponse<Boolean> errorResponse = ApiResponse.error(
                            "Invalid email format: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("User validation failed with unexpected error for email: {}, error: {}", 
                            email, ex.getMessage(), ex);
                    ApiResponse<Boolean> errorResponse = ApiResponse.error(
                            "An unexpected error occurred while validating the user");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }
    
    /**
     * Gets user information by email
     * 
     * @param email the email to search for
     * @return ResponseEntity containing user information
     */
    @GetMapping("/usuarios")
    @Operation(
        summary = "Get user by email",
        description = "Retrieves user information by email address"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> getUserByEmail(
            @Parameter(description = "Email address to search for", required = true)
            @RequestParam String email) {
        
        logger.info("Received get user request for email: {}", email);
        
        return userService.findUserByEmail(email)
                .map(user -> {
                    UserResponse response = userMapper.toResponse(user);
                    ApiResponse<UserResponse> apiResponse = ApiResponse.success(
                            "User found successfully", response);
                    
                    logger.info("User retrieved successfully for email: {}", email);
                    return ResponseEntity.ok(apiResponse);
                })
                .switchIfEmpty(Mono.fromCallable(() -> {
                    logger.info("User not found for email: {}", email);
                    ApiResponse<UserResponse> notFoundResponse = ApiResponse.error(
                            "User not found with email: " + email);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
                }))
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Get user failed with unexpected error for email: {}, error: {}", 
                            email, ex.getMessage(), ex);
                    ApiResponse<UserResponse> errorResponse = ApiResponse.error(
                            "An unexpected error occurred while retrieving the user");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }
}
