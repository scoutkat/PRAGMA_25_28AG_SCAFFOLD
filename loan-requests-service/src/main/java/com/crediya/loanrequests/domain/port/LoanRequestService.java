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
     * Validates user existence and loan type, then creates the request
     * @param loanRequest the loan request entity to create
     * @return Mono containing the created loan request with calculated details
     */
    Mono<LoanRequest> createLoanRequest(LoanRequest loanRequest);
    
    /**
     * Finds a loan request by ID
     * @param id the loan request ID to search for
     * @return Mono containing the loan request if found, empty if not found
     */
    Mono<LoanRequest> findLoanRequestById(Long id);
}