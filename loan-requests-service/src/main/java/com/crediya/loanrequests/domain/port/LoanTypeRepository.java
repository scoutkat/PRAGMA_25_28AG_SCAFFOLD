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
}