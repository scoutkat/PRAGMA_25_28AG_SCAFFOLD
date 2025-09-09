package com.crediya.loanrequests.application.service;

import com.crediya.loanrequests.domain.exception.InvalidLoanRequestException;
import com.crediya.loanrequests.domain.exception.LoanTypeNotFoundException;
import com.crediya.loanrequests.domain.exception.UserValidationException;
import com.crediya.loanrequests.domain.model.LoanRequest;
import com.crediya.loanrequests.domain.model.LoanType;
import com.crediya.loanrequests.domain.port.LoanRequestRepository;
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
    
    @BeforeEach
    void setUp() {
        loanRequestService = new LoanRequestServiceImpl(loanRequestRepository, loanTypeRepository, userValidationService);
    }
    
    @Test
    @DisplayName("Should create loan request successfully when user exists and loan type is valid")
    void shouldCreateLoanRequestSuccessfully() {
        // Given
        LoanRequest loanRequest = createTestLoanRequest();
        LoanType loanType = createTestLoanType();
        LoanRequest savedLoanRequest = createTestLoanRequest();
        savedLoanRequest.setId(1L);
        
        when(userValidationService.validateUserExists(anyString())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findActiveById(any(Long.class))).thenReturn(Mono.just(loanType));
        when(loanRequestRepository.save(any(LoanRequest.class))).thenReturn(Mono.just(savedLoanRequest));
        
        // When & Then
        StepVerifier.create(loanRequestService.createLoanRequest(loanRequest))
                .expectNext(savedLoanRequest)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should throw UserValidationException when user does not exist")
    void shouldThrowUserValidationExceptionWhenUserDoesNotExist() {
        // Given
        LoanRequest loanRequest = createTestLoanRequest();
        
        when(userValidationService.validateUserExists(anyString())).thenReturn(Mono.just(false));
        
        // When & Then
        StepVerifier.create(loanRequestService.createLoanRequest(loanRequest))
                .expectError(UserValidationException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Should throw LoanTypeNotFoundException when loan type is not found")
    void shouldThrowLoanTypeNotFoundExceptionWhenLoanTypeNotFound() {
        // Given
        LoanRequest loanRequest = createTestLoanRequest();
        
        when(userValidationService.validateUserExists(anyString())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findActiveById(any(Long.class))).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(loanRequestService.createLoanRequest(loanRequest))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Should throw InvalidLoanRequestException when amount is invalid")
    void shouldThrowInvalidLoanRequestExceptionWhenAmountIsInvalid() {
        // Given
        LoanRequest loanRequest = createTestLoanRequest();
        loanRequest.setAmount(new BigDecimal("50000")); // Below minimum
        LoanType loanType = createTestLoanType();
        
        when(userValidationService.validateUserExists(anyString())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findActiveById(any(Long.class))).thenReturn(Mono.just(loanType));
        
        // When & Then
        StepVerifier.create(loanRequestService.createLoanRequest(loanRequest))
                .expectError(InvalidLoanRequestException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Should throw InvalidLoanRequestException when term is invalid")
    void shouldThrowInvalidLoanRequestExceptionWhenTermIsInvalid() {
        // Given
        LoanRequest loanRequest = createTestLoanRequest();
        loanRequest.setTermMonths(3); // Below minimum
        LoanType loanType = createTestLoanType();
        
        when(userValidationService.validateUserExists(anyString())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findActiveById(any(Long.class))).thenReturn(Mono.just(loanType));
        
        // When & Then
        StepVerifier.create(loanRequestService.createLoanRequest(loanRequest))
                .expectError(InvalidLoanRequestException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Should find loan request by ID successfully")
    void shouldFindLoanRequestById() {
        // Given
        Long loanRequestId = 1L;
        LoanRequest loanRequest = createTestLoanRequest();
        loanRequest.setId(loanRequestId);
        
        when(loanRequestRepository.findById(any(Long.class))).thenReturn(Mono.just(loanRequest));
        
        // When & Then
        StepVerifier.create(loanRequestService.findLoanRequestById(loanRequestId))
                .expectNext(loanRequest)
                .verifyComplete();
    }
    
    /**
     * Helper method to create a test loan request
     * @return LoanRequest instance for testing
     */
    private LoanRequest createTestLoanRequest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setUserEmail("test@example.com");
        loanRequest.setLoanTypeId(1L);
        loanRequest.setAmount(new BigDecimal("1000000"));
        loanRequest.setTermMonths(12);
        return loanRequest;
    }
    
    /**
     * Helper method to create a test loan type
     * @return LoanType instance for testing
     */
    private LoanType createTestLoanType() {
        LoanType loanType = new LoanType();
        loanType.setId(1L);
        loanType.setName("Personal Loan");
        loanType.setMinAmount(new BigDecimal("100000"));
        loanType.setMaxAmount(new BigDecimal("10000000"));
        loanType.setMinTermMonths(6);
        loanType.setMaxTermMonths(60);
        loanType.setInterestRate(new BigDecimal("12.5"));
        loanType.setIsActive(true);
        return loanType;
    }
}
