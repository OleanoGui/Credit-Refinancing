package com.creditrefinancing.bff.service;

import com.creditrefinancing.bff.dto.SimulationRequestDTO;
import com.creditrefinancing.bff.dto.SimulationResponseDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Service
@Slf4j
public class SimulationService {

    @Value("${simulation.base-interest-rate:4.5}")
    private BigDecimal baseInterestRate;
    
    @Value("${simulation.max-debt-to-income-ratio:0.43}")
    private BigDecimal maxDebtToIncomeRatio;
    
    @Value("${simulation.processing-fee:0.01}")
    private BigDecimal processingFeeRate;

    public Mono<SimulationResponseDTO> calculateSimulation(SimulationRequestDTO request) {
        log.info("Calculating simulation for customer: {}", request.getCustomerId());
        
        return Mono.fromCallable(() -> {
            long startTime = System.currentTimeMillis();
            
            // Calculate interest rate based on risk factors
            BigDecimal interestRate = calculateInterestRate(request);
            
            // Calculate loan details
            BigDecimal monthlyPayment = calculateMonthlyPayment(
                    request.getDesiredLoanAmount(), 
                    interestRate,
                    request.getDesiredTermMonths()
            );
            
            BigDecimal totalInterest = calculateTotalInterest(
                    request.getDesiredLoanAmount(),
                    monthlyPayment,
                    request.getDesiredTermMonths()
            );
            
            BigDecimal totalAmount = request.getDesiredLoanAmount().add(totalInterest);
            BigDecimal monthlySavings = request.getCurrentMonthlyPayment().subtract(monthlyPayment);
            BigDecimal totalSavings = monthlySavings.multiply(BigDecimal.valueOf(request.getDesiredTermMonths()));
            
            // Risk assessment
            SimulationResponseDTO.RiskAssessment riskAssessment = calculateRiskAssessment(request);
            
            // Approval probability
            BigDecimal approvalProbability = calculateApprovalProbability(request, riskAssessment);
            
            // Current loan summary
            SimulationResponseDTO.CurrentLoanSummary currentLoanSummary = buildCurrentLoanSummary(request);
            
            // Payment schedule preview
            List<SimulationResponseDTO.PaymentPreview> paymentPreview = generatePaymentPreview(
                    request.getDesiredLoanAmount(), monthlyPayment, interestRate
            );
            
            // Comparison metrics
            SimulationResponseDTO.ComparisonMetrics comparisonMetrics = buildComparisonMetrics(
                    request, interestRate, monthlyPayment, totalSavings
            );
            
            // Next steps and conditions
            List<String> nextSteps = generateNextSteps(approvalProbability, riskAssessment);
            List<String> conditions = generateConditions(request, riskAssessment);
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            return SimulationResponseDTO.builder()
                    .simulationId("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .customerId(request.getCustomerId())
                    .status(determineStatus(approvalProbability))
                    .newLoanAmount(request.getDesiredLoanAmount())
                    .newMonthlyPayment(monthlyPayment)
                    .newInterestRate(interestRate)
                    .termMonths(request.getDesiredTermMonths())
                    .totalInterest(totalInterest)
                    .totalAmount(totalAmount)
                    .monthlySavings(monthlySavings)
                    .totalSavings(totalSavings)
                    .approvalProbability(approvalProbability)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusDays(30))
                    .loanType(request.getLoanType())
                    .currentLoanSummary(currentLoanSummary)
                    .riskAssessment(riskAssessment)
                    .paymentSchedulePreview(paymentPreview)
                    .comparisonMetrics(comparisonMetrics)
                    .nextSteps(nextSteps)
                    .conditions(conditions)
                    .processingTimeMs(processingTime)
                    .build();
        });
    }    public Mono<SimulationResponseDTO> getSimulation(String simulationId) {
        log.info("Getting simulation: {}", simulationId);
        
        // TODO: Integrate with simulation microservice to retrieve from database
        // For now, return a mock response with realistic data
        return Mono.just(
                SimulationResponseDTO.builder()
                        .simulationId(simulationId)
                        .customerId("CUST-MOCK-12345")
                        .status("CALCULATED")
                        .newLoanAmount(new BigDecimal("200000.00"))
                        .newMonthlyPayment(new BigDecimal("1150.00"))
                        .newInterestRate(new BigDecimal("4.75"))
                        .termMonths(240)
                        .createdAt(LocalDateTime.now().minusHours(1))
                        .expiresAt(LocalDateTime.now().plusDays(29).minusHours(1))
                        .build()
        );
    }

    // Advanced calculation methods
    
    private BigDecimal calculateInterestRate(SimulationRequestDTO request) {
        BigDecimal rate = baseInterestRate;
        
        // Adjust rate based on credit score
        if (request.getCreditScore() != null) {
            if (request.getCreditScore() >= 750) {
                rate = rate.subtract(new BigDecimal("0.5")); // Premium rate
            } else if (request.getCreditScore() < 650) {
                rate = rate.add(new BigDecimal("1.5")); // Higher risk rate
            } else if (request.getCreditScore() < 700) {
                rate = rate.add(new BigDecimal("0.75")); // Moderate risk rate
            }
        }
        
        // Adjust rate based on loan type
        switch (request.getLoanType()) {
            case "MORTGAGE" -> rate = rate.subtract(new BigDecimal("0.25"));
            case "PERSONAL" -> rate = rate.add(new BigDecimal("2.0"));
            case "AUTO" -> rate = rate.add(new BigDecimal("0.5"));
            case "BUSINESS" -> rate = rate.add(new BigDecimal("1.0"));
        }
        
        // Adjust based on loan amount (larger loans get better rates)
        if (request.getDesiredLoanAmount().compareTo(new BigDecimal("500000")) > 0) {
            rate = rate.subtract(new BigDecimal("0.25"));
        }
        
        return rate.max(new BigDecimal("2.0")); // Minimum rate of 2%
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal loanAmount, BigDecimal annualRate, Integer termMonths) {
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            return loanAmount.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        }
        
        // Convert annual rate to monthly rate
        BigDecimal monthlyRate = annualRate.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP)
                .divide(new BigDecimal("12"), 6, RoundingMode.HALF_UP);
        
        // Calculate monthly payment using standard loan formula
        // M = P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowerN = onePlusR.pow(termMonths);
        
        BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);
        
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalInterest(BigDecimal loanAmount, BigDecimal monthlyPayment, Integer termMonths) {
        BigDecimal totalPayments = monthlyPayment.multiply(BigDecimal.valueOf(termMonths));
        return totalPayments.subtract(loanAmount);
    }

    private SimulationResponseDTO.RiskAssessment calculateRiskAssessment(SimulationRequestDTO request) {
        List<String> riskFactors = new ArrayList<>();
        String riskLevel = "LOW";
        
        BigDecimal debtToIncomeRatio = BigDecimal.ZERO;
        if (request.getMonthlyIncome() != null && request.getMonthlyIncome().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newMonthlyPayment = calculateMonthlyPayment(
                    request.getDesiredLoanAmount(),
                    calculateInterestRate(request),
                    request.getDesiredTermMonths()
            );
            debtToIncomeRatio = newMonthlyPayment.divide(request.getMonthlyIncome(), 4, RoundingMode.HALF_UP);
            
            if (debtToIncomeRatio.compareTo(maxDebtToIncomeRatio) > 0) {
                riskFactors.add("Debt-to-income ratio exceeds recommended maximum");
                riskLevel = "HIGH";
            } else if (debtToIncomeRatio.compareTo(new BigDecimal("0.36")) > 0) {
                riskFactors.add("Debt-to-income ratio is elevated");
                riskLevel = "MEDIUM";
            }
        }
        
        if (request.getCreditScore() != null) {
            if (request.getCreditScore() < 650) {
                riskFactors.add("Credit score below recommended minimum");
                riskLevel = "HIGH";
            } else if (request.getCreditScore() < 700) {
                riskFactors.add("Credit score requires improvement");
                if (!"HIGH".equals(riskLevel)) riskLevel = "MEDIUM";
            }
        }
        
        if (request.getDesiredLoanAmount().compareTo(request.getCurrentLoanAmount().multiply(new BigDecimal("1.5"))) > 0) {
            riskFactors.add("Significant increase in loan amount");
            if (!"HIGH".equals(riskLevel)) riskLevel = "MEDIUM";
        }
        
        return SimulationResponseDTO.RiskAssessment.builder()
                .riskLevel(riskLevel)
                .debtToIncomeRatio(debtToIncomeRatio)
                .creditUtilization(new BigDecimal("0.35")) // Mock value
                .riskFactors(riskFactors)
                .build();
    }

    private BigDecimal calculateApprovalProbability(SimulationRequestDTO request, 
                                                  SimulationResponseDTO.RiskAssessment riskAssessment) {
        BigDecimal probability = new BigDecimal("0.85"); // Base probability
        
        // Adjust based on risk level
        switch (riskAssessment.getRiskLevel()) {
            case "HIGH" -> probability = new BigDecimal("0.45");
            case "MEDIUM" -> probability = new BigDecimal("0.70");
        }
        
        // Adjust based on credit score
        if (request.getCreditScore() != null) {
            if (request.getCreditScore() >= 750) {
                probability = probability.add(new BigDecimal("0.10"));
            } else if (request.getCreditScore() < 600) {
                probability = probability.subtract(new BigDecimal("0.20"));
            }
        }
        
        return probability.min(BigDecimal.ONE).max(BigDecimal.ZERO);
    }

    private SimulationResponseDTO.CurrentLoanSummary buildCurrentLoanSummary(SimulationRequestDTO request) {
        // Mock remaining term calculation - in reality this would come from current loan data
        Integer estimatedRemainingTerm = (int) (request.getCurrentLoanAmount()
                .divide(request.getCurrentMonthlyPayment(), 0, RoundingMode.HALF_UP).intValue() * 0.75);
        
        return SimulationResponseDTO.CurrentLoanSummary.builder()
                .remainingBalance(request.getCurrentLoanAmount())
                .currentRate(new BigDecimal("6.25")) // Mock current rate
                .remainingTermMonths(estimatedRemainingTerm)
                .build();
    }

    private List<SimulationResponseDTO.PaymentPreview> generatePaymentPreview(
            BigDecimal loanAmount, BigDecimal monthlyPayment, BigDecimal annualRate) {
        
        List<SimulationResponseDTO.PaymentPreview> preview = new ArrayList<>();
        BigDecimal monthlyRate = annualRate.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP)
                .divide(new BigDecimal("12"), 6, RoundingMode.HALF_UP);
        
        BigDecimal remainingBalance = loanAmount;
        
        for (int i = 1; i <= 3; i++) { // Show first 3 payments
            BigDecimal interestAmount = remainingBalance.multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalAmount = monthlyPayment.subtract(interestAmount);
            remainingBalance = remainingBalance.subtract(principalAmount);
            
            preview.add(SimulationResponseDTO.PaymentPreview.builder()
                    .paymentNumber(i)
                    .principalAmount(principalAmount)
                    .interestAmount(interestAmount)
                    .remainingBalance(remainingBalance)
                    .build());
        }
        
        return preview;
    }

    private SimulationResponseDTO.ComparisonMetrics buildComparisonMetrics(
            SimulationRequestDTO request, BigDecimal newRate, BigDecimal newPayment, BigDecimal totalSavings) {
        
        BigDecimal currentRate = new BigDecimal("6.25"); // Mock current rate
        BigDecimal rateDifference = newRate.subtract(currentRate);
        BigDecimal paymentDifference = newPayment.subtract(request.getCurrentMonthlyPayment());
        Integer breakEvenMonths = 8; // Mock break-even calculation
        
        return SimulationResponseDTO.ComparisonMetrics.builder()
                .rateDifference(rateDifference)
                .paymentDifference(paymentDifference)
                .totalCostDifference(totalSavings.negate())
                .breakEvenMonths(breakEvenMonths)
                .build();
    }

    private List<String> generateNextSteps(BigDecimal approvalProbability, 
                                         SimulationResponseDTO.RiskAssessment riskAssessment) {
        List<String> steps = new ArrayList<>();
        
        if (approvalProbability.compareTo(new BigDecimal("0.75")) >= 0) {
            steps.add("Submit formal loan application");
            steps.add("Prepare required documentation");
        } else if (approvalProbability.compareTo(new BigDecimal("0.50")) >= 0) {
            steps.add("Consider improving credit score first");
            steps.add("Review debt-to-income ratio");
            steps.add("Consult with loan advisor");
        } else {
            steps.add("Focus on improving creditworthiness");
            steps.add("Consider smaller loan amount");
            steps.add("Schedule consultation with financial advisor");
        }
        
        return steps;
    }

    private List<String> generateConditions(SimulationRequestDTO request, 
                                          SimulationResponseDTO.RiskAssessment riskAssessment) {
        List<String> conditions = new ArrayList<>();
        
        if ("HIGH".equals(riskAssessment.getRiskLevel())) {
            conditions.add("Higher down payment may be required");
            conditions.add("Additional income verification needed");
        }
        
        if (request.getCreditScore() != null && request.getCreditScore() < 700) {
            conditions.add("Credit score improvement recommended");
        }
        
        conditions.add("Property appraisal required");
        conditions.add("Employment verification needed");
        
        return conditions;
    }

    private String determineStatus(BigDecimal approvalProbability) {
        if (approvalProbability.compareTo(new BigDecimal("0.80")) >= 0) {
            return "CALCULATED";
        } else if (approvalProbability.compareTo(new BigDecimal("0.50")) >= 0) {
            return "PENDING";
        } else {
            return "REJECTED";
        }
    }
}
