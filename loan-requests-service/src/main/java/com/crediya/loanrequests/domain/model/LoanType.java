package com.crediya.loanrequests.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * LoanType domain entity representing different loan products offered by CrediYa
 * This class encapsulates loan type data and business rules for loan calculations
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
     * @return true if amount is within min and max range
     */
    public boolean isValidAmount(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        return amount.compareTo(minAmount) >= 0 && amount.compareTo(maxAmount) <= 0;
    }

    /**
     * Business method to validate if a loan term is within the allowed range
     * @param termMonths the loan term in months to validate
     * @return true if term is within min and max range
     */
    public boolean isValidTerm(Integer termMonths) {
        if (termMonths == null) {
            return false;
        }
        return termMonths >= minTermMonths && termMonths <= maxTermMonths;
    }

    /**
     * Business method to calculate monthly payment using compound interest formula
     * Formula: M = P * [r(1+r)^n] / [(1+r)^n - 1]
     * Where: M = Monthly payment, P = Principal amount, r = Monthly interest rate, n = Number of payments
     * @param principal the loan amount
     * @param termMonths the loan term in months
     * @return calculated monthly payment
     */
    public BigDecimal calculateMonthlyPayment(BigDecimal principal, Integer termMonths) {
        if (principal == null || termMonths == null || principal.compareTo(BigDecimal.ZERO) <= 0 || termMonths <= 0) {
            return BigDecimal.ZERO;
        }

        // Convert annual interest rate to monthly rate
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        // Calculate (1 + r)^n
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(termMonths);

        // Calculate monthly payment using the formula
        BigDecimal numerator = monthlyRate.multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);
        
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            // If denominator is zero, return simple division
            return principal.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        }

        return principal.multiply(numerator).divide(denominator, 2, RoundingMode.HALF_UP);
    }

    /**
     * Business method to calculate total interest over the loan term
     * @param principal the loan amount
     * @param termMonths the loan term in months
     * @return calculated total interest
     */
    public BigDecimal calculateTotalInterest(BigDecimal principal, Integer termMonths) {
        if (principal == null || termMonths == null || principal.compareTo(BigDecimal.ZERO) <= 0 || termMonths <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal monthlyPayment = calculateMonthlyPayment(principal, termMonths);
        BigDecimal totalPayments = monthlyPayment.multiply(BigDecimal.valueOf(termMonths));
        return totalPayments.subtract(principal);
    }

    /**
     * Business method to deactivate the loan type
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business method to update loan type information
     */
    public void update(String name, String description, BigDecimal minAmount, BigDecimal maxAmount,
                      Integer minTermMonths, Integer maxTermMonths, BigDecimal interestRate) {
        this.name = name;
        this.description = description;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.interestRate = interestRate;
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