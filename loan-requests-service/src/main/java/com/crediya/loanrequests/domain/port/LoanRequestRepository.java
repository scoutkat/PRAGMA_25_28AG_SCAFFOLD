package com.crediya.loanrequests.domain.port;

import com.crediya.loanrequests.domain.model.LoanRequest;
import reactor.core.publisher.Mono;

/**
 * Repository port interface for LoanRequest domain operations
 * This interface defines the contract for loan request data access
 * Following hexagonal architecture principles - this is a port in the domain layer
 */
public interface LoanRequestRepository {
    
    /**
     * Saves a new loan request to the database
     * @param loanRequest the loan request entity to save
     * @return Mono containing the saved loan request with generated ID
     */
    Mono<LoanRequest> save(LoanRequest loanRequest);
    
    /**
     * Finds a loan request by ID
     * @param id the loan request ID to search for
     * @return Mono containing the loan request if found, empty if not found
     */
    Mono<LoanRequest> findById(Long id);
    
    /**
     * Finds loan requests by user email
     * @param userEmail the user email to search for
     * @return Mono containing the loan request if found, empty if not found
     */
    Mono<LoanRequest> findByUserEmail(String userEmail);
    
    /**
     * Updates an existing loan request
     * @param loanRequest the loan request entity with updated information
     * @return Mono containing the updated loan request
     */
    Mono<LoanRequest> update(LoanRequest loanRequest);
    
    /**
     * Deletes a loan request by ID
     * @param id the loan request ID to delete
     * @return Mono containing true if deleted successfully, false otherwise
     */
    Mono<Boolean> deleteById(Long id);
}