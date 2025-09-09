package com.crediya.loanrequests.infrastructure.repository;

import com.crediya.loanrequests.domain.model.LoanType;
import com.crediya.loanrequests.domain.port.LoanTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * R2DBC implementation of LoanTypeRepository
 * This class handles database operations using reactive R2DBC
 * Following hexagonal architecture - this is an adapter in the infrastructure layer
 */
@Repository
public class R2DBCLoanTypeRepository implements LoanTypeRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(R2DBCLoanTypeRepository.class);
    
    private final DatabaseClient databaseClient;
    
    public R2DBCLoanTypeRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }
    
    @Override
    public Mono<LoanType> findById(Long id) {
        logger.debug("Finding loan type by ID: {}", id);
        
        String selectQuery = """
            SELECT id, name, description, min_amount, max_amount, min_term_months, max_term_months, 
                   interest_rate, is_active, created_at, updated_at
            FROM loan_types 
            WHERE id = :id
            """;
        
        return databaseClient.sql(selectQuery)
                .bind("id", id)
                .map((row, metadata) -> mapRowToLoanType(row))
                .one()
                .doOnSuccess(loanType -> {
                    if (loanType != null) {
                        logger.debug("Loan type found by ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error finding loan type by ID: {}, error: {}", 
                        id, error.getMessage()));
    }
    
    @Override
    public Mono<LoanType> findActiveById(Long id) {
        logger.debug("Finding active loan type by ID: {}", id);
        
        String selectQuery = """
            SELECT id, name, description, min_amount, max_amount, min_term_months, max_term_months, 
                   interest_rate, is_active, created_at, updated_at
            FROM loan_types 
            WHERE id = :id AND is_active = true
            """;
        
        return databaseClient.sql(selectQuery)
                .bind("id", id)
                .map((row, metadata) -> mapRowToLoanType(row))
                .one()
                .doOnSuccess(loanType -> {
                    if (loanType != null) {
                        logger.debug("Active loan type found by ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error finding active loan type by ID: {}, error: {}", 
                        id, error.getMessage()));
    }
    
    /**
     * Maps database row to LoanType entity
     * @param row the database row
     * @return LoanType entity
     */
    private LoanType mapRowToLoanType(org.springframework.r2dbc.core.Row row) {
        LoanType loanType = new LoanType();
        loanType.setId(row.get("id", Long.class));
        loanType.setName(row.get("name", String.class));
        loanType.setDescription(row.get("description", String.class));
        loanType.setMinAmount(row.get("min_amount", BigDecimal.class));
        loanType.setMaxAmount(row.get("max_amount", BigDecimal.class));
        loanType.setMinTermMonths(row.get("min_term_months", Integer.class));
        loanType.setMaxTermMonths(row.get("max_term_months", Integer.class));
        loanType.setInterestRate(row.get("interest_rate", BigDecimal.class));
        loanType.setIsActive(row.get("is_active", Boolean.class));
        loanType.setCreatedAt(row.get("created_at", LocalDateTime.class));
        loanType.setUpdatedAt(row.get("updated_at", LocalDateTime.class));
        return loanType;
    }
}
