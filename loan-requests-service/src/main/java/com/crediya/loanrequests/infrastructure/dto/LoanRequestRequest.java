package com.crediya.loanrequests.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for loan request creation request
 * Contains validation annotations for input validation
 * Used in the API layer to receive loan request data
 */
public class LoanRequestRequest {
    
    @NotBlank(message = "User email is required")
    @Email(message = "User email must be a valid email format")
    @Size(max = 255, message = "User email must not exceed 255 characters")
    private String userEmail;
    
    @NotNull(message = "Loan type ID is required")
    @Positive(message = "Loan type ID must be positive")
    private Long loanTypeId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.0", message = "Amount must be at least 1,000")
    @DecimalMax(value = "50000000.0", message = "Amount must not exceed 50,000,000")
    private BigDecimal amount;
    
    @NotNull(message = "Term in months is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    @Max(value = 360, message = "Term must not exceed 360 months")
    private Integer termMonths;
    
    // Default constructor
    public LoanRequestRequest() {}
    
    // Constructor with all fields
    public LoanRequestRequest(String userEmail, Long loanTypeId, BigDecimal amount, Integer termMonths) {
        this.userEmail = userEmail;
        this.loanTypeId = loanTypeId;
        this.amount = amount;
        this.termMonths = termMonths;
    }
    
    // Getters and Setters
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public Long getLoanTypeId() {
        return loanTypeId;
    }
    
    public void setLoanTypeId(Long loanTypeId) {
        this.loanTypeId = loanTypeId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Integer getTermMonths() {
        return termMonths;
    }
    
    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }
    
    @Override
    public String toString() {
        return "LoanRequestRequest{" +
                "userEmail='" + userEmail + '\'' +
                ", loanTypeId=" + loanTypeId +
                ", amount=" + amount +
                ", termMonths=" + termMonths +
                '}';
    }
}
