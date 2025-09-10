package com.crediya.loanrequests.domain.port;

import com.crediya.loanrequests.domain.model.LoanType;
import reactor.core.publisher.Mono;

/**
 * Repository port interface for LoanType domain operations
 * This interface defines the contract for loan type data access
 * Following hexagonal architecture principles - this is a port in the domain layer
 */
public interface LoanTypeRepository {
    
    /**
     * Finds a loan type by ID
     * @param id the loan type ID to search for
     * @return Mono containing the loan type if found, empty if not found
     */
    Mono<LoanType> findById(Long id);
    
    /**
     * Finds an active loan type by ID
     * @param id the loan type ID to search for
     * @return Mono containing the active loan type if found, empty if not found
     */
    Mono<LoanType> findActiveById(Long id);
    
    /**
     * Saves a new loan type to the database
     * @param loanType the loan type entity to save
     * @return Mono containing the saved loan type with generated ID
     */
    Mono<LoanType> save(LoanType loanType);
    
    /**
     * Updates an existing loan type
     * @param loanType the loan type entity with updated information
     * @return Mono containing the updated loan type
     */
    Mono<LoanType> update(LoanType loanType);
    
    /**
     * Deletes a loan type by ID
     * @param id the loan type ID to delete
     * @return Mono containing true if deleted successfully, false otherwise
     */
    Mono<Boolean> deleteById(Long id);
}