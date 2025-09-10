package com.crediya.loanrequests.domain.port;

import com.crediya.loanrequests.domain.model.LoanRequest;
import reactor.core.publisher.Mono;

/**
 * Service port interface for LoanRequest domain operations
 * This interface defines the business logic contract for loan request management
 * Following hexagonal architecture principles - this is a port in the domain layer
 */
public interface LoanRequestService {
    
    /**
     * Creates a new loan request
     * Validates business rules and ensures data integrity
     * @param loanRequest the loan request entity to create
     * @return Mono containing the created loan request with generated ID
     */
    Mono<LoanRequest> createLoanRequest(LoanRequest loanRequest);
    
    /**
     * Finds a loan request by ID
     * @param id the loan request ID to search for
     * @return Mono containing the loan request if found, empty if not found
     */
    Mono<LoanRequest> findLoanRequestById(Long id);
    
    /**
     * Finds loan requests by user email
     * @param userEmail the user email to search for
     * @return Mono containing the loan requests
     */
    Mono<LoanRequest> findLoanRequestsByUserEmail(String userEmail);
    
    /**
     * Updates a loan request status
     * @param id the loan request ID
     * @param status the new status
     * @param reviewedBy the user who reviewed the request
     * @param rejectionReason the reason for rejection (if applicable)
     * @return Mono containing the updated loan request
     */
    Mono<LoanRequest> updateLoanRequestStatus(Long id, LoanRequest.Status status, 
                                            String reviewedBy, String rejectionReason);
    
    /**
     * Validates if a loan request is valid
     * @param loanRequest the loan request to validate
     * @return Mono containing true if valid, false otherwise
     */
    Mono<Boolean> validateLoanRequest(LoanRequest loanRequest);
}