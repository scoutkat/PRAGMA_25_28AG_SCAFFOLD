package com.crediya.loanrequests.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * LoanType domain entity representing different loan products offered by CrediYa
 * This class encapsulates loan product information and business rules
 * Following Domain-Driven Design principles
 */
public class LoanType {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer minTermMonths;
    private Integer maxTermMonths;
    private BigDecimal interestRate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor for framework usage
    public LoanType() {}

    // Constructor for creating new loan types
    public LoanType(String name, String description, BigDecimal minAmount, BigDecimal maxAmount,
                   Integer minTermMonths, Integer maxTermMonths, BigDecimal interestRate) {
        this.name = name;
        this.description = description;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.interestRate = interestRate;
        this.isActive = true;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Integer getMinTermMonths() {
        return minTermMonths;
    }

    public void setMinTermMonths(Integer minTermMonths) {
        this.minTermMonths = minTermMonths;
    }

    public Integer getMaxTermMonths() {
        return maxTermMonths;
    }

    public void setMaxTermMonths(Integer maxTermMonths) {
        this.maxTermMonths = maxTermMonths;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    /**
     * Business method to validate if a loan amount is within the allowed range
     * @param amount the loan amount to validate
     * @return true if amount is valid, false otherwise
     */
    public boolean isValidAmount(BigDecimal amount) {
        return amount != null && 
               amount.compareTo(minAmount) >= 0 && 
               amount.compareTo(maxAmount) <= 0;
    }

    /**
     * Business method to validate if a loan term is within the allowed range
     * @param termMonths the loan term in months to validate
     * @return true if term is valid, false otherwise
     */
    public boolean isValidTerm(Integer termMonths) {
        return termMonths != null && 
               termMonths >= minTermMonths && 
               termMonths <= maxTermMonths;
    }

    /**
     * Business method to calculate monthly payment for a loan
     * Uses the standard loan payment formula
     * @param amount the loan amount
     * @param termMonths the loan term in months
     * @return calculated monthly payment
     */
    public BigDecimal calculateMonthlyPayment(BigDecimal amount, Integer termMonths) {
        if (amount == null || termMonths == null || termMonths <= 0) {
            return BigDecimal.ZERO;
        }

        // Convert annual interest rate to monthly rate
        BigDecimal monthlyRate = interestRate.divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP)
                .divide(new BigDecimal("12"), 6, BigDecimal.ROUND_HALF_UP);

        // Calculate monthly payment using the formula: P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal powerTerm = onePlusRate.pow(termMonths);
        BigDecimal numerator = monthlyRate.multiply(powerTerm);
        BigDecimal denominator = powerTerm.subtract(BigDecimal.ONE);
        
        return amount.multiply(numerator).divide(denominator, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Business method to calculate total interest for a loan
     * @param amount the loan amount
     * @param termMonths the loan term in months
     * @return calculated total interest
     */
    public BigDecimal calculateTotalInterest(BigDecimal amount, Integer termMonths) {
        BigDecimal monthlyPayment = calculateMonthlyPayment(amount, termMonths);
        BigDecimal totalPayment = monthlyPayment.multiply(new BigDecimal(termMonths));
        return totalPayment.subtract(amount);
    }

    /**
     * Business method to deactivate loan type
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "LoanType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", minAmount=" + minAmount +
                ", maxAmount=" + maxAmount +
                ", interestRate=" + interestRate +
                ", isActive=" + isActive +
                '}';
    }
}
