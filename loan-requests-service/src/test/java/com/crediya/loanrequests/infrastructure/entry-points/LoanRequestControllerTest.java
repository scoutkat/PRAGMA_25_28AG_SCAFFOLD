package com.crediya.loanrequests.infrastructure.entrypoints;

import com.crediya.loanrequests.domain.exception.InvalidLoanRequestException;
import com.crediya.loanrequests.domain.exception.LoanTypeNotFoundException;
import com.crediya.loanrequests.domain.exception.UserValidationException;
import com.crediya.loanrequests.domain.model.LoanRequest;
import com.crediya.loanrequests.domain.port.LoanRequestService;
import com.crediya.loanrequests.infrastructure.dto.ApiResponse;
import com.crediya.loanrequests.infrastructure.dto.LoanRequestRequest;
import com.crediya.loanrequests.infrastructure.dto.LoanRequestResponse;
import com.crediya.loanrequests.infrastructure.mapper.LoanRequestMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LoanRequestController
 * Tests the REST API layer with mocked service dependencies
 * Uses reactive testing with StepVerifier for WebFlux testing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Loan Request Controller Tests")
class LoanRequestControllerTest {

    @Mock
    private LoanRequestService loanRequestService;

    @Mock
    private LoanRequestMapper loanRequestMapper;

    private LoanRequestController loanRequestController;
    private LoanRequest testLoanRequest;
    private LoanRequestRequest testRequest;
    private LoanRequestResponse testResponse;

    @BeforeEach
    void setUp() {
        loanRequestController = new LoanRequestController(loanRequestService, loanRequestMapper);
        
        // Create test loan request
        testLoanRequest = new LoanRequest(
                "john.doe@example.com",
                1L,
                new BigDecimal("50000"),
                24
        );
        testLoanRequest.setId(1L);

        // Create test request
        testRequest = new LoanRequestRequest(
                "john.doe@example.com",
                1L,
                new BigDecimal("50000"),
                24
        );

        // Create test response
        testResponse = new LoanRequestResponse();
        testResponse.setId(1L);
        testResponse.setUserEmail("john.doe@example.com");
        testResponse.setLoanTypeId(1L);
        testResponse.setAmount(new BigDecimal("50000"));
        testResponse.setTermMonths(24);
        testResponse.setStatus("PENDING_REVIEW");
    }

    @Test
    @DisplayName("Should create loan request successfully")
    void shouldCreateLoanRequestSuccessfully() {
        // Given
        when(loanRequestMapper.toDomain(testRequest)).thenReturn(testLoanRequest);
        when(loanRequestService.createLoanRequest(testLoanRequest)).thenReturn(Mono.just(testLoanRequest));
        when(loanRequestMapper.toResponse(testLoanRequest)).thenReturn(testResponse);

        // When
        Mono<ResponseEntity<ApiResponse<LoanRequestResponse>>> result = loanRequestController.createLoanRequest(testRequest);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    assertTrue(response.getBody().isSuccess());
                    assertEquals("Loan request created successfully", response.getBody().getMessage());
                    assertNotNull(response.getBody().getData());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return not found when user validation fails")
    void shouldReturnNotFoundWhenUserValidationFails() {
        // Given
        when(loanRequestMapper.toDomain(testRequest)).thenReturn(testLoanRequest);
        when(loanRequestService.createLoanRequest(testLoanRequest))
                .thenReturn(Mono.error(new UserValidationException("User does not exist")));

        // When
        Mono<ResponseEntity<ApiResponse<LoanRequestResponse>>> result = loanRequestController.createLoanRequest(testRequest);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    assertFalse(response.getBody().isSuccess());
                    assertTrue(response.getBody().getMessage().contains("User validation failed"));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return not found when loan type not found")
    void shouldReturnNotFoundWhenLoanTypeNotFound() {
        // Given
        when(loanRequestMapper.toDomain(testRequest)).thenReturn(testLoanRequest);
        when(loanRequestService.createLoanRequest(testLoanRequest))
                .thenReturn(Mono.error(new LoanTypeNotFoundException("Loan type not found")));

        // When
        Mono<ResponseEntity<ApiResponse<LoanRequestResponse>>> result = loanRequestController.createLoanRequest(testRequest);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    assertFalse(response.getBody().isSuccess());
                    assertTrue(response.getBody().getMessage().contains("Loan type not found"));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return bad request for validation errors")
    void shouldReturnBadRequestForValidationErrors() {
        // Given
        when(loanRequestMapper.toDomain(testRequest)).thenReturn(testLoanRequest);
        when(loanRequestService.createLoanRequest(testLoanRequest))
                .thenReturn(Mono.error(new InvalidLoanRequestException("Invalid loan request data")));

        // When
        Mono<ResponseEntity<ApiResponse<LoanRequestResponse>>> result = loanRequestController.createLoanRequest(testRequest);

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
    @DisplayName("Should get loan request by ID successfully")
    void shouldGetLoanRequestByIdSuccessfully() {
        // Given
        Long requestId = 1L;
        when(loanRequestService.findLoanRequestById(requestId)).thenReturn(Mono.just(testLoanRequest));
        when(loanRequestMapper.toResponse(testLoanRequest)).thenReturn(testResponse);

        // When
        Mono<ResponseEntity<ApiResponse<LoanRequestResponse>>> result = loanRequestController.getLoanRequestById(requestId);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertTrue(response.getBody().isSuccess());
                    assertEquals("Loan request found successfully", response.getBody().getMessage());
                    assertNotNull(response.getBody().getData());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return not found when loan request does not exist")
    void shouldReturnNotFoundWhenLoanRequestDoesNotExist() {
        // Given
        Long requestId = 1L;
        when(loanRequestService.findLoanRequestById(requestId)).thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<ApiResponse<LoanRequestResponse>>> result = loanRequestController.getLoanRequestById(requestId);

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
    @DisplayName("Should get loan request by user email successfully")
    void shouldGetLoanRequestByUserEmailSuccessfully() {
        // Given
        String userEmail = "john.doe@example.com";
        when(loanRequestService.findLoanRequestsByUserEmail(userEmail)).thenReturn(Mono.just(testLoanRequest));
        when(loanRequestMapper.toResponse(testLoanRequest)).thenReturn(testResponse);

        // When
        Mono<ResponseEntity<ApiResponse<LoanRequestResponse>>> result = loanRequestController.getLoanRequestByUserEmail(userEmail);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertTrue(response.getBody().isSuccess());
                    assertEquals("Loan request found successfully", response.getBody().getMessage());
                    assertNotNull(response.getBody().getData());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle internal server errors gracefully")
    void shouldHandleInternalServerErrors() {
        // Given
        when(loanRequestMapper.toDomain(testRequest)).thenReturn(testLoanRequest);
        when(loanRequestService.createLoanRequest(testLoanRequest))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        // When
        Mono<ResponseEntity<ApiResponse<LoanRequestResponse>>> result = loanRequestController.createLoanRequest(testRequest);

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
