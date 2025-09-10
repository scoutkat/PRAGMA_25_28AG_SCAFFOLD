package com.crediya.loanrequests.application.service;

import com.crediya.loanrequests.domain.exception.InvalidLoanRequestException;
import com.crediya.loanrequests.domain.exception.LoanTypeNotFoundException;
import com.crediya.loanrequests.domain.exception.UserValidationException;
import com.crediya.loanrequests.domain.model.LoanRequest;
import com.crediya.loanrequests.domain.model.LoanType;
import com.crediya.loanrequests.domain.port.LoanRequestRepository;
import com.crediya.loanrequests.domain.port.LoanRequestService;
import com.crediya.loanrequests.domain.port.LoanTypeRepository;
import com.crediya.loanrequests.domain.port.UserValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LoanRequestServiceImpl
 * Tests the business logic layer with mocked dependencies
 * Uses reactive testing with StepVerifier for WebFlux testing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Loan Request Service Implementation Tests")
class LoanRequestServiceImplTest {

    @Mock
    private LoanRequestRepository loanRequestRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private UserValidationService userValidationService;

    private LoanRequestServiceImpl loanRequestService;
    private LoanRequest testLoanRequest;
    private LoanType testLoanType;

    @BeforeEach
    void setUp() {
        loanRequestService = new LoanRequestServiceImpl(loanRequestRepository, loanTypeRepository, userValidationService);
        
        // Create test loan type
        testLoanType = new LoanType(
                "Personal Loan",
                "Personal loan for general purposes",
                new BigDecimal("1000"),
                new BigDecimal("10000000"),
                6,
                60,
                new BigDecimal("12.50")
        );
        testLoanType.setId(1L);

        // Create test loan request
        testLoanRequest = new LoanRequest(
                "john.doe@example.com",
                1L,
                new BigDecimal("50000"),
                24
        );
        testLoanRequest.setId(1L);
    }

    @Test
    @DisplayName("Should create loan request successfully when validation passes")
    void shouldCreateLoanRequestSuccessfully() {
        // Given
        when(userValidationService.validateUserExists(testLoanRequest.getUserEmail())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findActiveById(testLoanRequest.getLoanTypeId())).thenReturn(Mono.just(testLoanType));
        when(loanRequestRepository.save(any(LoanRequest.class))).thenReturn(Mono.just(testLoanRequest));

        // When & Then
        StepVerifier.create(loanRequestService.createLoanRequest(testLoanRequest))
                .expectNext(testLoanRequest)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UserValidationException when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // Given
        when(userValidationService.validateUserExists(testLoanRequest.getUserEmail())).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(loanRequestService.createLoanRequest(testLoanRequest))
                .expectError(UserValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw LoanTypeNotFoundException when loan type does not exist")
    void shouldThrowExceptionWhenLoanTypeDoesNotExist() {
        // Given
        when(userValidationService.validateUserExists(testLoanRequest.getUserEmail())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findActiveById(testLoanRequest.getLoanTypeId())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(loanRequestService.createLoanRequest(testLoanRequest))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw InvalidLoanRequestException when amount is invalid")
    void shouldThrowExceptionWhenAmountIsInvalid() {
        // Given
        testLoanRequest.setAmount(new BigDecimal("500")); // Below minimum
        when(userValidationService.validateUserExists(testLoanRequest.getUserEmail())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findActiveById(testLoanRequest.getLoanTypeId())).thenReturn(Mono.just(testLoanType));

        // When & Then
        StepVerifier.create(loanRequestService.createLoanRequest(testLoanRequest))
                .expectError(InvalidLoanRequestException.class)
                .verify();
    }

    @Test
    @DisplayName("Should find loan request by ID successfully")
    void shouldFindLoanRequestById() {
        // Given
        Long requestId = 1L;
        when(loanRequestRepository.findById(requestId)).thenReturn(Mono.just(testLoanRequest));

        // When & Then
        StepVerifier.create(loanRequestService.findLoanRequestById(requestId))
                .expectNext(testLoanRequest)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find loan requests by user email successfully")
    void shouldFindLoanRequestsByUserEmail() {
        // Given
        String userEmail = "john.doe@example.com";
        when(loanRequestRepository.findByUserEmail(userEmail)).thenReturn(Mono.just(testLoanRequest));

        // When & Then
        StepVerifier.create(loanRequestService.findLoanRequestsByUserEmail(userEmail))
                .expectNext(testLoanRequest)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update loan request status successfully")
    void shouldUpdateLoanRequestStatus() {
        // Given
        Long requestId = 1L;
        when(loanRequestRepository.findById(requestId)).thenReturn(Mono.just(testLoanRequest));
        when(loanRequestRepository.update(any(LoanRequest.class))).thenReturn(Mono.just(testLoanRequest));

        // When & Then
        StepVerifier.create(loanRequestService.updateLoanRequestStatus(requestId, 
                LoanRequest.Status.APPROVED, "admin", null))
                .expectNext(testLoanRequest)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should validate loan request successfully")
    void shouldValidateLoanRequestSuccessfully() {
        // Given
        when(userValidationService.validateUserExists(testLoanRequest.getUserEmail())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findActiveById(testLoanRequest.getLoanTypeId())).thenReturn(Mono.just(testLoanType));

        // When & Then
        StepVerifier.create(loanRequestService.validateLoanRequest(testLoanRequest))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle repository errors gracefully")
    void shouldHandleRepositoryErrors() {
        // Given
        when(userValidationService.validateUserExists(anyString())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        StepVerifier.create(loanRequestService.createLoanRequest(testLoanRequest))
                .expectError(RuntimeException.class)
                .verify();
    }
}
