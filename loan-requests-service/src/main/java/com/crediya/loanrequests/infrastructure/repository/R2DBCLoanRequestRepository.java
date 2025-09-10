package com.crediya.loanrequests.infrastructure.repository;

import com.crediya.loanrequests.domain.model.LoanRequest;
import com.crediya.loanrequests.domain.port.LoanRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * R2DBC implementation of LoanRequestRepository
 * This class handles database operations using reactive R2DBC
 * Following hexagonal architecture - this is an adapter in the infrastructure layer
 */
@Repository
public class R2DBCLoanRequestRepository implements LoanRequestRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(R2DBCLoanRequestRepository.class);
    
    private final DatabaseClient databaseClient;
    
    public R2DBCLoanRequestRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }
    
    @Override
    public Mono<LoanRequest> save(LoanRequest loanRequest) {
        logger.debug("Saving loan request to database for user: {}", loanRequest.getUserEmail());
        
        String insertQuery = """
            INSERT INTO loan_requests (user_email, loan_type_id, amount, term_months, status, 
                                     monthly_payment, total_interest, total_amount, created_at, updated_at, 
                                     reviewed_at, reviewed_by, rejection_reason)
            VALUES (:userEmail, :loanTypeId, :amount, :termMonths, :status, :monthlyPayment, 
                    :totalInterest, :totalAmount, :createdAt, :updatedAt, :reviewedAt, :reviewedBy, :rejectionReason)
            """;
        
        return databaseClient.sql(insertQuery)
                .bind("userEmail", loanRequest.getUserEmail())
                .bind("loanTypeId", loanRequest.getLoanTypeId())
                .bind("amount", loanRequest.getAmount())
                .bind("termMonths", loanRequest.getTermMonths())
                .bind("status", loanRequest.getStatus() != null ? loanRequest.getStatus().name() : null)
                .bind("monthlyPayment", loanRequest.getMonthlyPayment())
                .bind("totalInterest", loanRequest.getTotalInterest())
                .bind("totalAmount", loanRequest.getTotalAmount())
                .bind("createdAt", loanRequest.getCreatedAt())
                .bind("updatedAt", loanRequest.getUpdatedAt())
                .bind("reviewedAt", loanRequest.getReviewedAt())
                .bind("reviewedBy", loanRequest.getReviewedBy())
                .bind("rejectionReason", loanRequest.getRejectionReason())
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id"))
                .map((row, metadata) -> {
                    loanRequest.setId(row.get("id", Long.class));
                    return loanRequest;
                })
                .one()
                .doOnSuccess(savedRequest -> logger.debug("Loan request saved successfully with ID: {}", 
                        savedRequest.getId()))
                .doOnError(error -> logger.error("Error saving loan request: {}", error.getMessage()));
    }
    
    @Override
    public Mono<LoanRequest> findById(Long id) {
        logger.debug("Finding loan request by ID: {}", id);
        
        String selectQuery = """
            SELECT id, user_email, loan_type_id, amount, term_months, status, monthly_payment, 
                   total_interest, total_amount, created_at, updated_at, reviewed_at, reviewed_by, rejection_reason
            FROM loan_requests 
            WHERE id = :id
            """;
        
        return databaseClient.sql(selectQuery)
                .bind("id", id)
                .map((row, metadata) -> mapRowToLoanRequest(row))
                .one()
                .doOnSuccess(request -> {
                    if (request != null) {
                        logger.debug("Loan request found by ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error finding loan request by ID: {}, error: {}", 
                        id, error.getMessage()));
    }
    
    @Override
    public Mono<LoanRequest> findByUserEmail(String userEmail) {
        logger.debug("Finding loan request by user email: {}", userEmail);
        
        String selectQuery = """
            SELECT id, user_email, loan_type_id, amount, term_months, status, monthly_payment, 
                   total_interest, total_amount, created_at, updated_at, reviewed_at, reviewed_by, rejection_reason
            FROM loan_requests 
            WHERE user_email = :userEmail
            ORDER BY created_at DESC
            LIMIT 1
            """;
        
        return databaseClient.sql(selectQuery)
                .bind("userEmail", userEmail)
                .map((row, metadata) -> mapRowToLoanRequest(row))
                .one()
                .doOnSuccess(request -> {
                    if (request != null) {
                        logger.debug("Loan request found for user: {}", userEmail);
                    }
                })
                .doOnError(error -> logger.error("Error finding loan request for user: {}, error: {}", 
                        userEmail, error.getMessage()));
    }
    
    @Override
    public Mono<LoanRequest> update(LoanRequest loanRequest) {
        logger.debug("Updating loan request: {}", loanRequest.getId());
        
        String updateQuery = """
            UPDATE loan_requests 
            SET user_email = :userEmail, loan_type_id = :loanTypeId, amount = :amount, 
                term_months = :termMonths, status = :status, monthly_payment = :monthlyPayment, 
                total_interest = :totalInterest, total_amount = :totalAmount, updated_at = :updatedAt, 
                reviewed_at = :reviewedAt, reviewed_by = :reviewedBy, rejection_reason = :rejectionReason
            WHERE id = :id
            """;
        
        return databaseClient.sql(updateQuery)
                .bind("id", loanRequest.getId())
                .bind("userEmail", loanRequest.getUserEmail())
                .bind("loanTypeId", loanRequest.getLoanTypeId())
                .bind("amount", loanRequest.getAmount())
                .bind("termMonths", loanRequest.getTermMonths())
                .bind("status", loanRequest.getStatus() != null ? loanRequest.getStatus().name() : null)
                .bind("monthlyPayment", loanRequest.getMonthlyPayment())
                .bind("totalInterest", loanRequest.getTotalInterest())
                .bind("totalAmount", loanRequest.getTotalAmount())
                .bind("updatedAt", loanRequest.getUpdatedAt())
                .bind("reviewedAt", loanRequest.getReviewedAt())
                .bind("reviewedBy", loanRequest.getReviewedBy())
                .bind("rejectionReason", loanRequest.getRejectionReason())
                .then(Mono.just(loanRequest))
                .doOnSuccess(updatedRequest -> logger.debug("Loan request updated successfully: {}", 
                        updatedRequest.getId()))
                .doOnError(error -> logger.error("Error updating loan request: {}, error: {}", 
                        loanRequest.getId(), error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> deleteById(Long id) {
        logger.debug("Deleting loan request by ID: {}", id);
        
        String deleteQuery = "DELETE FROM loan_requests WHERE id = :id";
        
        return databaseClient.sql(deleteQuery)
                .bind("id", id)
                .fetch()
                .rowsUpdated()
                .map(rowsUpdated -> rowsUpdated > 0)
                .doOnSuccess(deleted -> logger.debug("Loan request deletion result for ID {}: {}", id, deleted))
                .doOnError(error -> logger.error("Error deleting loan request by ID: {}, error: {}", 
                        id, error.getMessage()));
    }
    
    /**
     * Maps database row to LoanRequest entity
     * @param row the database row
     * @return LoanRequest entity
     */
    private LoanRequest mapRowToLoanRequest(org.springframework.r2dbc.core.Row row) {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setId(row.get("id", Long.class));
        loanRequest.setUserEmail(row.get("user_email", String.class));
        loanRequest.setLoanTypeId(row.get("loan_type_id", Long.class));
        loanRequest.setAmount(row.get("amount", BigDecimal.class));
        loanRequest.setTermMonths(row.get("term_months", Integer.class));
        
        String statusStr = row.get("status", String.class);
        if (statusStr != null) {
            loanRequest.setStatus(LoanRequest.Status.valueOf(statusStr));
        }
        
        loanRequest.setMonthlyPayment(row.get("monthly_payment", BigDecimal.class));
        loanRequest.setTotalInterest(row.get("total_interest", BigDecimal.class));
        loanRequest.setTotalAmount(row.get("total_amount", BigDecimal.class));
        loanRequest.setCreatedAt(row.get("created_at", LocalDateTime.class));
        loanRequest.setUpdatedAt(row.get("updated_at", LocalDateTime.class));
        loanRequest.setReviewedAt(row.get("reviewed_at", LocalDateTime.class));
        loanRequest.setReviewedBy(row.get("reviewed_by", String.class));
        loanRequest.setRejectionReason(row.get("rejection_reason", String.class));
        
        return loanRequest;
    }
}
