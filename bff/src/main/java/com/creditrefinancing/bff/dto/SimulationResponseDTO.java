package com.creditrefinancing.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationResponseDTO {
    
    @JsonProperty("simulation_id")
    private String simulationId;
    
    @JsonProperty("customer_id")
    private String customerId;
    
    @JsonProperty("status")
    private String status; // CALCULATED, APPROVED, REJECTED
    
    @JsonProperty("new_loan_amount")
    private BigDecimal newLoanAmount;
    
    @JsonProperty("new_monthly_payment")
    private BigDecimal newMonthlyPayment;
    
    @JsonProperty("new_interest_rate")
    private BigDecimal newInterestRate;
    
    @JsonProperty("term_months")
    private Integer termMonths;
    
    @JsonProperty("total_interest")
    private BigDecimal totalInterest;
    
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    
    @JsonProperty("monthly_savings")
    private BigDecimal monthlySavings;
    
    @JsonProperty("total_savings")
    private BigDecimal totalSavings;
    
    @JsonProperty("approval_probability")
    private BigDecimal approvalProbability; // 0.0 to 1.0
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;
    
    @JsonProperty("loan_type")
    private String loanType;
}
