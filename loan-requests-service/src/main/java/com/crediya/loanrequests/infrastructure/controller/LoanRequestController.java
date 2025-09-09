package com.crediya.loanrequests.infrastructure.controller;

import com.crediya.loanrequests.domain.exception.InvalidLoanRequestException;
import com.crediya.loanrequests.domain.exception.LoanTypeNotFoundException;
import com.crediya.loanrequests.domain.exception.UserValidationException;
import com.crediya.loanrequests.domain.model.LoanRequest;
import com.crediya.loanrequests.domain.port.LoanRequestService;
import com.crediya.loanrequests.infrastructure.dto.ApiResponse;
import com.crediya.loanrequests.infrastructure.dto.LoanRequestRequest;
import com.crediya.loanrequests.infrastructure.dto.LoanRequestResponse;
import com.crediya.loanrequests.infrastructure.mapper.LoanRequestMapper;
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
 * REST Controller for loan request management operations
 * Handles HTTP requests and responses using reactive programming with WebFlux
 * Provides endpoints for loan request creation and retrieval
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Loan Request Management", description = "APIs for managing loan requests in the CrediYa platform")
public class LoanRequestController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanRequestController.class);
    
    private final LoanRequestService loanRequestService;
    private final LoanRequestMapper loanRequestMapper;
    
    public LoanRequestController(LoanRequestService loanRequestService, LoanRequestMapper loanRequestMapper) {
        this.loanRequestService = loanRequestService;
        this.loanRequestMapper = loanRequestMapper;
    }
    
    /**
     * Creates a new loan request
     * Validates user existence and loan type, then creates the request
     * 
     * @param request the loan request creation request
     * @return ResponseEntity containing the created loan request information
     */
    @PostMapping("/solicitud")
    @Operation(
        summary = "Create a new loan request",
        description = "Creates a new loan request with the provided information. " +
                     "Validates that the user exists and the loan type is valid. " +
                     "Automatically calculates loan details based on the loan type."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Loan request created successfully",
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
            responseCode = "404",
            description = "User not found or loan type not found",
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
    public Mono<ResponseEntity<ApiResponse<LoanRequestResponse>>> createLoanRequest(
            @Valid @RequestBody LoanRequestRequest request) {
        
        logger.info("Received loan request creation request for user: {}", request.getUserEmail());
        
        return loanRequestService.createLoanRequest(loanRequestMapper.toDomain(request))
                .map(loanRequest -> {
                    LoanRequestResponse response = loanRequestMapper.toResponse(loanRequest);
                    ApiResponse<LoanRequestResponse> apiResponse = ApiResponse.success(
                            "Loan request created successfully", response);
                    
                    logger.info("Loan request creation completed successfully for user: {}, ID: {}", 
                            request.getUserEmail(), loanRequest.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
                })
                .onErrorResume(UserValidationException.class, ex -> {
                    logger.warn("Loan request creation failed - user validation error: {}", ex.getMessage());
                    ApiResponse<LoanRequestResponse> errorResponse = ApiResponse.error(
                            "User validation failed: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
                })
                .onErrorResume(LoanTypeNotFoundException.class, ex -> {
                    logger.warn("Loan request creation failed - loan type not found: {}", ex.getMessage());
                    ApiResponse<LoanRequestResponse> errorResponse = ApiResponse.error(
                            "Loan type not found: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
                })
                .onErrorResume(InvalidLoanRequestException.class, ex -> {
                    logger.warn("Loan request creation failed - invalid loan request: {}", ex.getMessage());
                    ApiResponse<LoanRequestResponse> errorResponse = ApiResponse.error(
                            "Invalid loan request: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                })
                .onErrorResume(IllegalArgumentException.class, ex -> {
                    logger.warn("Loan request creation failed - validation error: {}", ex.getMessage());
                    ApiResponse<LoanRequestResponse> errorResponse = ApiResponse.error(
                            "Validation error: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Loan request creation failed with unexpected error for user: {}, error: {}", 
                            request.getUserEmail(), ex.getMessage(), ex);
                    ApiResponse<LoanRequestResponse> errorResponse = ApiResponse.error(
                            "An unexpected error occurred while creating the loan request");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }
    
    /**
     * Gets loan request information by ID
     * 
     * @param id the loan request ID to search for
     * @return ResponseEntity containing loan request information
     */
    @GetMapping("/solicitud/{id}")
    @Operation(
        summary = "Get loan request by ID",
        description = "Retrieves loan request information by ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Loan request found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Loan request not found",
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
    public Mono<ResponseEntity<ApiResponse<LoanRequestResponse>>> getLoanRequestById(
            @Parameter(description = "Loan request ID to search for", required = true)
            @PathVariable Long id) {
        
        logger.info("Received get loan request request for ID: {}", id);
        
        return loanRequestService.findLoanRequestById(id)
                .map(loanRequest -> {
                    LoanRequestResponse response = loanRequestMapper.toResponse(loanRequest);
                    ApiResponse<LoanRequestResponse> apiResponse = ApiResponse.success(
                            "Loan request found successfully", response);
                    
                    logger.info("Loan request retrieved successfully for ID: {}", id);
                    return ResponseEntity.ok(apiResponse);
                })
                .switchIfEmpty(Mono.fromCallable(() -> {
                    logger.info("Loan request not found for ID: {}", id);
                    ApiResponse<LoanRequestResponse> notFoundResponse = ApiResponse.error(
                            "Loan request not found with ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
                }))
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Get loan request failed with unexpected error for ID: {}, error: {}", 
                            id, ex.getMessage(), ex);
                    ApiResponse<LoanRequestResponse> errorResponse = ApiResponse.error(
                            "An unexpected error occurred while retrieving the loan request");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }
}
