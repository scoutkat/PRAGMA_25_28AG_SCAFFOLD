package com.crediya.loanrequests.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * LoanRequest domain entity representing a loan application
 * This class encapsulates loan request data and business rules
 * Following Domain-Driven Design principles
 */
public class LoanRequest {
    
    public enum Status {
        PENDING_REVIEW("Pending Review"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        Status(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private Long id;
    private String userEmail;
    private Long loanTypeId;
    private BigDecimal amount;
    private Integer termMonths;
    private Status status;
    private BigDecimal monthlyPayment;
    private BigDecimal totalInterest;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String rejectionReason;

    // Default constructor for framework usage
    public LoanRequest() {}

    // Constructor for creating new loan requests
    public LoanRequest(String userEmail, Long loanTypeId, BigDecimal amount, Integer termMonths) {
        this.userEmail = userEmail;
        this.loanTypeId = loanTypeId;
        this.amount = amount;
        this.termMonths = termMonths;
        this.status = Status.PENDING_REVIEW;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    /**
     * Business method to calculate loan details based on loan type
     * @param loanType the loan type containing interest rate and terms
     */
    public void calculateLoanDetails(LoanType loanType) {
        if (loanType == null) {
            return;
        }
        
        this.monthlyPayment = loanType.calculateMonthlyPayment(this.amount, this.termMonths);
        this.totalInterest = loanType.calculateTotalInterest(this.amount, this.termMonths);
        this.totalAmount = this.amount.add(this.totalInterest);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business method to approve the loan request
     * @param reviewedBy the user who approved the request
     */
    public void approve(String reviewedBy) {
        this.status = Status.APPROVED;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business method to reject the loan request
     * @param reviewedBy the user who rejected the request
     * @param rejectionReason the reason for rejection
     */
    public void reject(String reviewedBy, String rejectionReason) {
        this.status = Status.REJECTED;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = LocalDateTime.now();
        this.rejectionReason = rejectionReason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business method to cancel the loan request
     */
    public void cancel() {
        this.status = Status.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business method to check if the request is pending
     * @return true if status is PENDING_REVIEW
     */
    public boolean isPending() {
        return Status.PENDING_REVIEW.equals(this.status);
    }

    /**
     * Business method to check if the request is approved
     * @return true if status is APPROVED
     */
    public boolean isApproved() {
        return Status.APPROVED.equals(this.status);
    }

    /**
     * Business method to check if the request is rejected
     * @return true if status is REJECTED
     */
    public boolean isRejected() {
        return Status.REJECTED.equals(this.status);
    }

    @Override
    public String toString() {
        return "LoanRequest{" +
                "id=" + id +
                ", userEmail='" + userEmail + '\'' +
                ", loanTypeId=" + loanTypeId +
                ", amount=" + amount +
                ", termMonths=" + termMonths +
                ", status=" + status +
                '}';
    }
}
