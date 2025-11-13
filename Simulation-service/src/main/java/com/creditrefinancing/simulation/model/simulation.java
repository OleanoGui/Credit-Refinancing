package com.creditrefinancing.simulation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("simulations")
public class Simulation {

    @Id
    private Long id;
    
    private String simulationId;
    private String customerId;
    
    // Loan Information
    private BigDecimal currentLoanAmount;
    private BigDecimal requestedAmount;
    private Integer termInMonths;
    private String loanType;
    
    // Customer Financial Information
    private BigDecimal monthlyIncome;
    private Integer creditScore;
    private Boolean hasGoodPaymentHistory;
    
    // Calculated Results
    private BigDecimal interestRate;
    private BigDecimal monthlyPayment;
    private BigDecimal totalAmount;
    private BigDecimal totalInterest;
    private BigDecimal processingFee;
    
    // Risk Assessment
    private String riskLevel;
    private BigDecimal debtToIncomeRatio;
    private BigDecimal approvalProbability;
    
    // Status and Metadata
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    
    // Additional Information
    private String notes;
}