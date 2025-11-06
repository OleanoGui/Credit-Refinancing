package com.creditrefinancing.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response containing credit refinancing simulation results")
public class SimulationResponseDTO {
    
    @JsonProperty("simulation_id")
    @Schema(description = "Unique simulation identifier", example = "SIM-123456789")
    private String simulationId;
    
    @JsonProperty("customer_id")
    @Schema(description = "Customer identifier", example = "CUST-12345")
    private String customerId;
    
    @JsonProperty("status")
    @Schema(description = "Simulation status", example = "CALCULATED", 
            allowableValues = {"CALCULATED", "APPROVED", "REJECTED", "EXPIRED", "PENDING"})
    private String status;
    
    @JsonProperty("new_loan_amount")
    @Schema(description = "New loan amount after refinancing", example = "200000.00")
    private BigDecimal newLoanAmount;
    
    @JsonProperty("new_monthly_payment")
    @Schema(description = "New monthly payment amount", example = "1150.00")
    private BigDecimal newMonthlyPayment;
    
    @JsonProperty("new_interest_rate")
    @Schema(description = "New interest rate (annual percentage)", example = "4.50")
    private BigDecimal newInterestRate;
    
    @JsonProperty("term_months")
    @Schema(description = "Loan term in months", example = "240")
    private Integer termMonths;
    
    @JsonProperty("total_interest")
    @Schema(description = "Total interest amount over loan term", example = "76000.00")
    private BigDecimal totalInterest;
    
    @JsonProperty("total_amount")
    @Schema(description = "Total amount to be paid (principal + interest)", example = "276000.00")
    private BigDecimal totalAmount;
    
    @JsonProperty("monthly_savings")
    @Schema(description = "Monthly savings compared to current loan", example = "50.50")
    private BigDecimal monthlySavings;
    
    @JsonProperty("total_savings")
    @Schema(description = "Total savings over loan term", example = "12120.00")
    private BigDecimal totalSavings;
    
    @JsonProperty("approval_probability")
    @Schema(description = "Probability of loan approval (0.0 to 1.0)", example = "0.85")
    private BigDecimal approvalProbability;
    
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Simulation creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @JsonProperty("expires_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Simulation expiration timestamp", example = "2024-02-15T10:30:00")
    private LocalDateTime expiresAt;
    
    @JsonProperty("loan_type")
    @Schema(description = "Type of loan", example = "MORTGAGE")
    private String loanType;
    
    // Additional fields for enhanced response
    @JsonProperty("current_loan_summary")
    @Schema(description = "Summary of current loan details")
    private CurrentLoanSummary currentLoanSummary;
    
    @JsonProperty("risk_assessment")
    @Schema(description = "Risk assessment details")
    private RiskAssessment riskAssessment;
    
    @JsonProperty("payment_schedule_preview")
    @Schema(description = "Preview of first few payments")
    private List<PaymentPreview> paymentSchedulePreview;
    
    @JsonProperty("comparison_metrics")
    @Schema(description = "Detailed comparison with current loan")
    private ComparisonMetrics comparisonMetrics;
    
    @JsonProperty("next_steps")
    @Schema(description = "Recommended next steps for the customer")
    private List<String> nextSteps;
    
    @JsonProperty("conditions")
    @Schema(description = "Special conditions or requirements")
    private List<String> conditions;
    
    @JsonProperty("processing_time_ms")
    @Schema(description = "Time taken to process simulation in milliseconds", example = "150")
    private Long processingTimeMs;
    
    // Nested DTOs
    @Data
    @Builder
    @Schema(description = "Current loan summary")
    public static class CurrentLoanSummary {
        @JsonProperty("remaining_balance")
        @Schema(description = "Remaining balance on current loan", example = "150000.00")
        private BigDecimal remainingBalance;
        
        @JsonProperty("current_rate")
        @Schema(description = "Current interest rate", example = "6.25")
        private BigDecimal currentRate;
        
        @JsonProperty("remaining_term_months")
        @Schema(description = "Remaining term in months", example = "180")
        private Integer remainingTermMonths;
    }
    
    @Data
    @Builder
    @Schema(description = "Risk assessment details")
    public static class RiskAssessment {
        @JsonProperty("risk_level")
        @Schema(description = "Overall risk level", example = "LOW", 
                allowableValues = {"LOW", "MEDIUM", "HIGH"})
        private String riskLevel;
        
        @JsonProperty("debt_to_income_ratio")
        @Schema(description = "Debt to income ratio", example = "0.28")
        private BigDecimal debtToIncomeRatio;
        
        @JsonProperty("credit_utilization")
        @Schema(description = "Credit utilization percentage", example = "0.35")
        private BigDecimal creditUtilization;
        
        @JsonProperty("risk_factors")
        @Schema(description = "List of identified risk factors")
        private List<String> riskFactors;
    }
    
    @Data
    @Builder
    @Schema(description = "Payment schedule preview")
    public static class PaymentPreview {
        @JsonProperty("payment_number")
        @Schema(description = "Payment number", example = "1")
        private Integer paymentNumber;
        
        @JsonProperty("principal_amount")
        @Schema(description = "Principal portion", example = "650.00")
        private BigDecimal principalAmount;
        
        @JsonProperty("interest_amount")
        @Schema(description = "Interest portion", example = "500.00")
        private BigDecimal interestAmount;
        
        @JsonProperty("remaining_balance")
        @Schema(description = "Remaining balance after payment", example = "199350.00")
        private BigDecimal remainingBalance;
    }
    
    @Data
    @Builder
    @Schema(description = "Comparison metrics with current loan")
    public static class ComparisonMetrics {
        @JsonProperty("rate_difference")
        @Schema(description = "Interest rate difference (new - current)", example = "-1.75")
        private BigDecimal rateDifference;
        
        @JsonProperty("payment_difference")
        @Schema(description = "Monthly payment difference", example = "-50.50")
        private BigDecimal paymentDifference;
        
        @JsonProperty("total_cost_difference")
        @Schema(description = "Total cost difference over loan term", example = "-12120.00")
        private BigDecimal totalCostDifference;
        
        @JsonProperty("break_even_months")
        @Schema(description = "Months to break even on refinancing costs", example = "8")
        private Integer breakEvenMonths;
    }
}
