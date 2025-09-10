package com.crediya.loanrequests.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for loan request response data
 * Used to return loan request information in API responses
 * Excludes sensitive information and provides a clean API interface
 */
public class LoanRequestResponse {
    
    private Long id;
    private String userEmail;
    private Long loanTypeId;
    private BigDecimal amount;
    private Integer termMonths;
    private String status;
    private String statusDisplayName;
    private BigDecimal monthlyPayment;
    private BigDecimal totalInterest;
    private BigDecimal totalAmount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;
    
    private String reviewedBy;
    private String rejectionReason;
    
    // Default constructor
    public LoanRequestResponse() {}
    
    // Constructor with all fields
    public LoanRequestResponse(Long id, String userEmail, Long loanTypeId, BigDecimal amount,
                              Integer termMonths, String status, String statusDisplayName,
                              BigDecimal monthlyPayment, BigDecimal totalInterest, BigDecimal totalAmount,
                              LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime reviewedAt,
                              String reviewedBy, String rejectionReason) {
        this.id = id;
        this.userEmail = userEmail;
        this.loanTypeId = loanTypeId;
        this.amount = amount;
        this.termMonths = termMonths;
        this.status = status;
        this.statusDisplayName = statusDisplayName;
        this.monthlyPayment = monthlyPayment;
        this.totalInterest = totalInterest;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reviewedAt = reviewedAt;
        this.reviewedBy = reviewedBy;
        this.rejectionReason = rejectionReason;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatusDisplayName() {
        return statusDisplayName;
    }
    
    public void setStatusDisplayName(String statusDisplayName) {
        this.statusDisplayName = statusDisplayName;
    }
    
    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }
    
    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }
    
    public BigDecimal getTotalInterest() {
        return totalInterest;
    }
    
    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
    public String getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    @Override
    public String toString() {
        return "LoanRequestResponse{" +
                "id=" + id +
                ", userEmail='" + userEmail + '\'' +
                ", loanTypeId=" + loanTypeId +
                ", amount=" + amount +
                ", termMonths=" + termMonths +
                ", status='" + status + '\'' +
                '}';
    }
}
