package com.creditrefinancing.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to calculate credit refinancing simulation")
public class SimulationRequestDTO {
    
    @NotBlank(message = "Customer ID is required and cannot be blank")
    @Size(min = 3, max = 50, message = "Customer ID must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Customer ID can only contain letters, numbers, hyphens and underscores")
    @JsonProperty("customer_id")
    @Schema(description = "Unique customer identifier", example = "CUST-12345")
    private String customerId;
    
    @NotNull(message = "Current loan amount is required")
    @DecimalMin(value = "1000.00", message = "Current loan amount must be at least $1,000")
    @DecimalMax(value = "10000000.00", message = "Current loan amount cannot exceed $10,000,000")
    @Digits(integer = 10, fraction = 2, message = "Current loan amount must have at most 2 decimal places")
    @JsonProperty("current_loan_amount")
    @Schema(description = "Current outstanding loan amount", example = "150000.00")
    private BigDecimal currentLoanAmount;
    
    @NotNull(message = "Current monthly payment is required")
    @DecimalMin(value = "50.00", message = "Current monthly payment must be at least $50")
    @DecimalMax(value = "100000.00", message = "Current monthly payment cannot exceed $100,000")
    @Digits(integer = 8, fraction = 2, message = "Current monthly payment must have at most 2 decimal places")
    @JsonProperty("current_monthly_payment")
    @Schema(description = "Current monthly loan payment", example = "1200.50")
    private BigDecimal currentMonthlyPayment;
    
    @NotNull(message = "Desired loan amount is required")
    @DecimalMin(value = "1000.00", message = "Desired loan amount must be at least $1,000")
    @DecimalMax(value = "10000000.00", message = "Desired loan amount cannot exceed $10,000,000")
    @Digits(integer = 10, fraction = 2, message = "Desired loan amount must have at most 2 decimal places")
    @JsonProperty("desired_loan_amount")
    @Schema(description = "Desired new loan amount", example = "200000.00")
    private BigDecimal desiredLoanAmount;
    
    @NotNull(message = "Desired term in months is required")
    @Min(value = 12, message = "Loan term must be at least 12 months")
    @Max(value = 360, message = "Loan term cannot exceed 360 months (30 years)")
    @JsonProperty("desired_term_months")
    @Schema(description = "Desired loan term in months", example = "240")
    private Integer desiredTermMonths;
    
    @NotBlank(message = "Loan type is required")
    @Pattern(regexp = "^(PERSONAL|MORTGAGE|AUTO|BUSINESS|STUDENT)$", 
             message = "Loan type must be one of: PERSONAL, MORTGAGE, AUTO, BUSINESS, STUDENT")
    @JsonProperty("loan_type")
    @Schema(description = "Type of loan", example = "MORTGAGE", allowableValues = {"PERSONAL", "MORTGAGE", "AUTO", "BUSINESS", "STUDENT"})
    private String loanType;
    
    @DecimalMin(value = "0.00", message = "Monthly income cannot be negative")
    @DecimalMax(value = "1000000.00", message = "Monthly income cannot exceed $1,000,000")
    @Digits(integer = 8, fraction = 2, message = "Monthly income must have at most 2 decimal places")
    @JsonProperty("monthly_income")
    @Schema(description = "Customer's monthly income", example = "5000.00")
    private BigDecimal monthlyIncome;
    
    @Min(value = 300, message = "Credit score minimum is 300")
    @Max(value = 850, message = "Credit score maximum is 850")
    @JsonProperty("credit_score")
    @Schema(description = "Customer's credit score", example = "720")
    private Integer creditScore;
}
