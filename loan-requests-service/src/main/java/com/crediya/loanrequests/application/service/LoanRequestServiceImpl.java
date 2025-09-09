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
        
        // Validate user exists
        return userValidationService.validateUserExists(loanRequest.getUserEmail())
                .flatMap(userExists -> {
                    if (!userExists) {
                        logger.warn("Loan request creation failed - user does not exist: {}", loanRequest.getUserEmail());
                        return Mono.error(new UserValidationException("User with email " + loanRequest.getUserEmail() + " does not exist"));
                    }
                    
                    logger.info("User validation passed, proceeding with loan type validation: {}", loanRequest.getLoanTypeId());
                    return loanTypeRepository.findActiveById(loanRequest.getLoanTypeId());
                })
                .flatMap(loanType -> {
                    if (loanType == null) {
                        logger.warn("Loan request creation failed - loan type not found or inactive: {}", loanRequest.getLoanTypeId());
                        return Mono.error(new LoanTypeNotFoundException("Loan type with ID " + loanRequest.getLoanTypeId() + " not found or inactive"));
                    }
                    
                    // Validate loan amount and term against loan type constraints
                    if (!loanType.isValidAmount(loanRequest.getAmount())) {
                        logger.warn("Loan request creation failed - invalid amount: {} (min: {}, max: {})", 
                                loanRequest.getAmount(), loanType.getMinAmount(), loanType.getMaxAmount());
                        return Mono.error(new InvalidLoanRequestException(
                                "Loan amount must be between " + loanType.getMinAmount() + " and " + loanType.getMaxAmount()));
                    }
                    
                    if (!loanType.isValidTerm(loanRequest.getTermMonths())) {
                        logger.warn("Loan request creation failed - invalid term: {} months (min: {}, max: {})", 
                                loanRequest.getTermMonths(), loanType.getMinTermMonths(), loanType.getMaxTermMonths());
                        return Mono.error(new InvalidLoanRequestException(
                                "Loan term must be between " + loanType.getMinTermMonths() + " and " + loanType.getMaxTermMonths() + " months"));
                    }
                    
                    // Calculate loan details using the loan type
                    loanRequest.calculateLoanDetails(loanType);
                    
                    logger.info("Loan request validation passed, saving to database: {}", loanRequest.getUserEmail());
                    return loanRequestRepository.save(loanRequest);
                })
                .doOnSuccess(savedLoanRequest -> logger.info("Loan request created successfully with ID: {}", savedLoanRequest.getId()))
                .doOnError(error -> logger.error("Loan request creation failed for user: {}, error: {}", 
                        loanRequest.getUserEmail(), error.getMessage()));
    }
    
    @Override
    public Mono<LoanRequest> findLoanRequestById(Long id) {
        logger.debug("Searching for loan request by ID: {}", id);
        
        return loanRequestRepository.findById(id)
                .doOnSuccess(loanRequest -> {
                    if (loanRequest != null) {
                        logger.debug("Loan request found by ID: {}", id);
                    } else {
                        logger.debug("No loan request found with ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error searching loan request by ID: {}, error: {}", 
                        id, error.getMessage()));
    }
}
