package com.creditrefinancing.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationRequestDTO {
    
    @NotNull(message = "Customer ID is required")
    @JsonProperty("customer_id")
    private String customerId;
    
    @NotNull(message = "Current loan amount is required")
    @DecimalMin(value = "1000.0", message = "Current loan amount must be at least 1000")
    @JsonProperty("current_loan_amount")
    private BigDecimal currentLoanAmount;
    
    @NotNull(message = "Current monthly payment is required")
    @DecimalMin(value = "100.0", message = "Current monthly payment must be at least 100")
    @JsonProperty("current_monthly_payment")
    private BigDecimal currentMonthlyPayment;
    
    @NotNull(message = "Desired loan amount is required")
    @DecimalMin(value = "1000.0", message = "Desired loan amount must be at least 1000")
    @JsonProperty("desired_loan_amount")
    private BigDecimal desiredLoanAmount;
    
    @NotNull(message = "Desired term in months is required")
    @Min(value = 12, message = "Term must be at least 12 months")
    @Max(value = 360, message = "Term cannot exceed 360 months")
    @JsonProperty("desired_term_months")
    private Integer desiredTermMonths;
    
    @NotBlank(message = "Loan type is required")
    @JsonProperty("loan_type")
    private String loanType;
    
    @JsonProperty("income")
    @DecimalMin(value = "0.0", message = "Income cannot be negative")
    private BigDecimal income;
    
    @JsonProperty("credit_score")
    @Min(value = 300, message = "Credit score minimum is 300")
    @Max(value = 850, message = "Credit score maximum is 850")
    private Integer creditScore;
}
