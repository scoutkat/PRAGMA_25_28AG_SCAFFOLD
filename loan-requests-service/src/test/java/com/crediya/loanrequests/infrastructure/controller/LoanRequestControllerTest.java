package com.crediya.loanrequests.infrastructure.controller;

import com.crediya.loanrequests.domain.exception.InvalidLoanRequestException;
import com.crediya.loanrequests.domain.exception.LoanTypeNotFoundException;
import com.crediya.loanrequests.domain.exception.UserValidationException;
import com.crediya.loanrequests.domain.model.LoanRequest;
import com.crediya.loanrequests.domain.port.LoanRequestService;
import com.crediya.loanrequests.infrastructure.dto.LoanRequestRequest;
import com.crediya.loanrequests.infrastructure.entry-points.LoanRequestController;
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
    
    @BeforeEach
    void setUp() {
        loanRequestController = new LoanRequestController(loanRequestService, loanRequestMapper);
    }
    
    @Test
    @DisplayName("Should create loan request successfully and return 201 status")
    void shouldCreateLoanRequestSuccessfully() {
        // Given
        LoanRequestRequest request = createTestLoanRequestRequest();
        LoanRequest loanRequest = createTestLoanRequest();
        loanRequest.setId(1L);
        
        when(loanRequestMapper.toDomain(any(LoanRequestRequest.class))).thenReturn(loanRequest);
        when(loanRequestService.createLoanRequest(any(LoanRequest.class))).thenReturn(Mono.just(loanRequest));
        when(loanRequestMapper.toResponse(any(LoanRequest.class))).thenReturn(createTestLoanRequestResponse());
        
        // When & Then
        StepVerifier.create(loanRequestController.createLoanRequest(request))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.CREATED &&
                        response.getBody() != null &&
                        response.getBody().isSuccess())
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should return 404 status when user validation fails")
    void shouldReturn404WhenUserValidationFails() {
        // Given
        LoanRequestRequest request = createTestLoanRequestRequest();
        LoanRequest loanRequest = createTestLoanRequest();
        
        when(loanRequestMapper.toDomain(any(LoanRequestRequest.class))).thenReturn(loanRequest);
        when(loanRequestService.createLoanRequest(any(LoanRequest.class)))
                .thenReturn(Mono.error(new UserValidationException("User not found")));
        
        // When & Then
        StepVerifier.create(loanRequestController.createLoanRequest(request))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.NOT_FOUND &&
                        response.getBody() != null &&
                        !response.getBody().isSuccess())
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should return 404 status when loan type not found")
    void shouldReturn404WhenLoanTypeNotFound() {
        // Given
        LoanRequestRequest request = createTestLoanRequestRequest();
        LoanRequest loanRequest = createTestLoanRequest();
        
        when(loanRequestMapper.toDomain(any(LoanRequestRequest.class))).thenReturn(loanRequest);
        when(loanRequestService.createLoanRequest(any(LoanRequest.class)))
                .thenReturn(Mono.error(new LoanTypeNotFoundException("Loan type not found")));
        
        // When & Then
        StepVerifier.create(loanRequestController.createLoanRequest(request))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.NOT_FOUND &&
                        response.getBody() != null &&
                        !response.getBody().isSuccess())
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should return 400 status when loan request is invalid")
    void shouldReturn400WhenLoanRequestIsInvalid() {
        // Given
        LoanRequestRequest request = createTestLoanRequestRequest();
        LoanRequest loanRequest = createTestLoanRequest();
        
        when(loanRequestMapper.toDomain(any(LoanRequestRequest.class))).thenReturn(loanRequest);
        when(loanRequestService.createLoanRequest(any(LoanRequest.class)))
                .thenReturn(Mono.error(new InvalidLoanRequestException("Invalid amount")));
        
        // When & Then
        StepVerifier.create(loanRequestController.createLoanRequest(request))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.BAD_REQUEST &&
                        response.getBody() != null &&
                        !response.getBody().isSuccess())
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should get loan request by ID and return 200 status")
    void shouldGetLoanRequestById() {
        // Given
        Long loanRequestId = 1L;
        LoanRequest loanRequest = createTestLoanRequest();
        loanRequest.setId(loanRequestId);
        
        when(loanRequestService.findLoanRequestById(anyLong())).thenReturn(Mono.just(loanRequest));
        when(loanRequestMapper.toResponse(any(LoanRequest.class))).thenReturn(createTestLoanRequestResponse());
        
        // When & Then
        StepVerifier.create(loanRequestController.getLoanRequestById(loanRequestId))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.OK &&
                        response.getBody() != null &&
                        response.getBody().isSuccess())
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should return 404 when loan request not found")
    void shouldReturn404WhenLoanRequestNotFound() {
        // Given
        Long loanRequestId = 1L;
        
        when(loanRequestService.findLoanRequestById(anyLong())).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(loanRequestController.getLoanRequestById(loanRequestId))
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.NOT_FOUND &&
                        response.getBody() != null &&
                        !response.getBody().isSuccess())
                .verifyComplete();
    }
    
    /**
     * Helper method to create a test loan request request
     * @return LoanRequestRequest for testing
     */
    private LoanRequestRequest createTestLoanRequestRequest() {
        LoanRequestRequest request = new LoanRequestRequest();
        request.setUserEmail("test@example.com");
        request.setLoanTypeId(1L);
        request.setAmount(new BigDecimal("1000000"));
        request.setTermMonths(12);
        return request;
    }
    
    /**
     * Helper method to create a test loan request
     * @return LoanRequest instance for testing
     */
    private LoanRequest createTestLoanRequest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setId(1L);
        loanRequest.setUserEmail("test@example.com");
        loanRequest.setLoanTypeId(1L);
        loanRequest.setAmount(new BigDecimal("1000000"));
        loanRequest.setTermMonths(12);
        return loanRequest;
    }
    
    /**
     * Helper method to create a test loan request response
     * @return LoanRequestResponse for testing
     */
    private com.crediya.loanrequests.infrastructure.dto.LoanRequestResponse createTestLoanRequestResponse() {
        com.crediya.loanrequests.infrastructure.dto.LoanRequestResponse response = 
                new com.crediya.loanrequests.infrastructure.dto.LoanRequestResponse();
        response.setId(1L);
        response.setUserEmail("test@example.com");
        response.setLoanTypeId(1L);
        response.setAmount(new BigDecimal("1000000"));
        response.setTermMonths(12);
        response.setStatus("PENDING_REVIEW");
        return response;
    }
}
