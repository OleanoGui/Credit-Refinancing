package com.creditrefinancing.bff.service;

import com.creditrefinancing.bff.dto.SimulationRequestDTO;
import com.creditrefinancing.bff.dto.SimulationResponseDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class SimulationService {

    public Mono<SimulationResponseDTO> calculateSimulation(SimulationRequestDTO request) {
        log.info("Calculating simulation for customer: {}", request.getCustomerId());
        
        return Mono.fromCallable(() -> {
            // TODO: Integrate with simulation microservice
            // For now, return a mock response
            
            return SimulationResponseDTO.builder()
                    .simulationId(UUID.randomUUID().toString())
                    .customerId(request.getCustomerId())
                    .status("CALCULATED")
                    .newLoanAmount(request.getDesiredLoanAmount())
                    .newMonthlyPayment(calculateMonthlyPayment(
                            request.getDesiredLoanAmount(), 
                            request.getDesiredTermMonths()
                    ))
                    .newInterestRate(BigDecimal.valueOf(4.5)) // Mock rate
                    .termMonths(request.getDesiredTermMonths())
                    .totalInterest(calculateTotalInterest(
                            request.getDesiredLoanAmount(),
                            request.getDesiredTermMonths()
                    ))
                    .totalAmount(calculateTotalAmount(
                            request.getDesiredLoanAmount(),
                            request.getDesiredTermMonths()
                    ))
                    .monthlySavings(calculateMonthlySavings(
                            request.getCurrentMonthlyPayment(),
                            request.getDesiredLoanAmount(),
                            request.getDesiredTermMonths()
                    ))
                    .approvalProbability(BigDecimal.valueOf(0.85)) // Mock probability
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusDays(30))
                    .loanType(request.getLoanType())
                    .build();
        });
    }

    public Mono<SimulationResponseDTO> getSimulation(String simulationId) {
        log.info("Getting simulation: {}", simulationId);
        
        // TODO: Integrate with simulation microservice
        // For now, return a mock response
        return Mono.just(
                SimulationResponseDTO.builder()
                        .simulationId(simulationId)
                        .customerId("mock-customer-id")
                        .status("CALCULATED")
                        .createdAt(LocalDateTime.now().minusHours(1))
                        .build()
        );
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal loanAmount, Integer termMonths) {
        // Simple calculation - in real scenario this would be more complex
        BigDecimal monthlyRate = BigDecimal.valueOf(0.045).divide(BigDecimal.valueOf(12), 10, BigDecimal.ROUND_HALF_UP);
        // Simplified payment calculation
        return loanAmount.multiply(monthlyRate.add(BigDecimal.ONE))
                .divide(BigDecimal.valueOf(termMonths), 2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateTotalInterest(BigDecimal loanAmount, Integer termMonths) {
        BigDecimal monthlyPayment = calculateMonthlyPayment(loanAmount, termMonths);
        return monthlyPayment.multiply(BigDecimal.valueOf(termMonths)).subtract(loanAmount);
    }

    private BigDecimal calculateTotalAmount(BigDecimal loanAmount, Integer termMonths) {
        return loanAmount.add(calculateTotalInterest(loanAmount, termMonths));
    }

    private BigDecimal calculateMonthlySavings(BigDecimal currentPayment, BigDecimal newLoanAmount, Integer termMonths) {
        BigDecimal newPayment = calculateMonthlyPayment(newLoanAmount, termMonths);
        return currentPayment.subtract(newPayment);
    }
}
