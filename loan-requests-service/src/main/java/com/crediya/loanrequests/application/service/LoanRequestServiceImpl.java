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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of LoanRequestService following hexagonal architecture
 * This class contains the business logic for loan request management
 * Uses reactive programming with WebFlux
 */
@Service
public class LoanRequestServiceImpl implements LoanRequestService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanRequestServiceImpl.class);
    
    private final LoanRequestRepository loanRequestRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserValidationService userValidationService;
    
    public LoanRequestServiceImpl(LoanRequestRepository loanRequestRepository,
                                LoanTypeRepository loanTypeRepository,
                                UserValidationService userValidationService) {
        this.loanRequestRepository = loanRequestRepository;
        this.loanTypeRepository = loanTypeRepository;
        this.userValidationService = userValidationService;
    }
    
    @Override
    public Mono<LoanRequest> createLoanRequest(LoanRequest loanRequest) {
        logger.info("Starting loan request creation process for user: {}", loanRequest.getUserEmail());
        
        return validateLoanRequest(loanRequest)
                .flatMap(isValid -> {
                    if (!isValid) {
                        logger.warn("Loan request validation failed for user: {}", loanRequest.getUserEmail());
                        return Mono.error(new InvalidLoanRequestException("Invalid loan request data"));
                    }
                    
                    logger.info("Loan request validation passed, proceeding with creation for user: {}", 
                            loanRequest.getUserEmail());
                    return loanRequestRepository.save(loanRequest);
                })
                .doOnSuccess(savedRequest -> logger.info("Loan request created successfully with ID: {}", 
                        savedRequest.getId()))
                .doOnError(error -> logger.error("Loan request creation failed for user: {}, error: {}", 
                        loanRequest.getUserEmail(), error.getMessage()));
    }
    
    @Override
    public Mono<LoanRequest> findLoanRequestById(Long id) {
        logger.debug("Searching for loan request by ID: {}", id);
        
        return loanRequestRepository.findById(id)
                .doOnSuccess(request -> {
                    if (request != null) {
                        logger.debug("Loan request found by ID: {}", id);
                    } else {
                        logger.debug("No loan request found with ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error searching loan request by ID: {}, error: {}", 
                        id, error.getMessage()));
    }
    
    @Override
    public Mono<LoanRequest> findLoanRequestsByUserEmail(String userEmail) {
        logger.debug("Searching for loan requests by user email: {}", userEmail);
        
        return loanRequestRepository.findByUserEmail(userEmail)
                .doOnSuccess(request -> {
                    if (request != null) {
                        logger.debug("Loan request found for user: {}", userEmail);
                    } else {
                        logger.debug("No loan request found for user: {}", userEmail);
                    }
                })
                .doOnError(error -> logger.error("Error searching loan requests for user: {}, error: {}", 
                        userEmail, error.getMessage()));
    }
    
    @Override
    public Mono<LoanRequest> updateLoanRequestStatus(Long id, LoanRequest.Status status, 
                                                   String reviewedBy, String rejectionReason) {
        logger.info("Starting loan request status update for ID: {}, status: {}", id, status);
        
        return loanRequestRepository.findById(id)
                .switchIfEmpty(Mono.error(new InvalidLoanRequestException("Loan request not found with ID: " + id)))
                .flatMap(loanRequest -> {
                    switch (status) {
                        case APPROVED -> loanRequest.approve(reviewedBy);
                        case REJECTED -> loanRequest.reject(reviewedBy, rejectionReason);
                        case CANCELLED -> loanRequest.cancel();
                        default -> {
                            logger.warn("Invalid status update for loan request ID: {}", id);
                            return Mono.error(new InvalidLoanRequestException("Invalid status: " + status));
                        }
                    }
                    
                    return loanRequestRepository.update(loanRequest);
                })
                .doOnSuccess(updatedRequest -> logger.info("Loan request status updated successfully: {}", 
                        updatedRequest.getId()))
                .doOnError(error -> logger.error("Loan request status update failed for ID: {}, error: {}", 
                        id, error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> validateLoanRequest(LoanRequest loanRequest) {
        logger.debug("Validating loan request for user: {}", loanRequest.getUserEmail());
        
        return userValidationService.validateUserExists(loanRequest.getUserEmail())
                .flatMap(userExists -> {
                    if (!userExists) {
                        logger.warn("User validation failed - user does not exist: {}", loanRequest.getUserEmail());
                        return Mono.error(new UserValidationException("User does not exist: " + loanRequest.getUserEmail()));
                    }
                    
                    return loanTypeRepository.findActiveById(loanRequest.getLoanTypeId());
                })
                .flatMap(loanType -> {
                    if (loanType == null) {
                        logger.warn("Loan type validation failed - loan type not found: {}", loanRequest.getLoanTypeId());
                        return Mono.error(new LoanTypeNotFoundException("Loan type not found: " + loanRequest.getLoanTypeId()));
                    }
                    
                    // Validate loan amount and term against loan type constraints
                    boolean isValidAmount = loanType.isValidAmount(loanRequest.getAmount());
                    boolean isValidTerm = loanType.isValidTerm(loanRequest.getTermMonths());
                    
                    if (!isValidAmount) {
                        logger.warn("Loan amount validation failed: {} not in range [{}, {}]", 
                                loanRequest.getAmount(), loanType.getMinAmount(), loanType.getMaxAmount());
                        return Mono.error(new InvalidLoanRequestException(
                                "Loan amount must be between " + loanType.getMinAmount() + " and " + loanType.getMaxAmount()));
                    }
                    
                    if (!isValidTerm) {
                        logger.warn("Loan term validation failed: {} not in range [{}, {}]", 
                                loanRequest.getTermMonths(), loanType.getMinTermMonths(), loanType.getMaxTermMonths());
                        return Mono.error(new InvalidLoanRequestException(
                                "Loan term must be between " + loanType.getMinTermMonths() + " and " + loanType.getMaxTermMonths() + " months"));
                    }
                    
                    // Calculate loan details
                    loanRequest.calculateLoanDetails(loanType);
                    
                    logger.debug("Loan request validation passed for user: {}", loanRequest.getUserEmail());
                    return Mono.just(true);
                })
                .doOnError(error -> logger.error("Loan request validation failed for user: {}, error: {}", 
                        loanRequest.getUserEmail(), error.getMessage()));
    }
}
