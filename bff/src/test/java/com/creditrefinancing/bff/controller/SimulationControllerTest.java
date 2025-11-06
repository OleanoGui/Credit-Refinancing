package com.creditrefinancing.bff.controller;

import com.creditrefinancing.bff.dto.SimulationRequestDTO;
import com.creditrefinancing.bff.dto.SimulationResponseDTO;
import com.creditrefinancing.bff.service.SimulationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(SimulationController.class)
@DisplayName("Simulation Controller Tests")
class SimulationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SimulationService simulationService;

    @Autowired
    private ObjectMapper objectMapper;

    private SimulationRequestDTO validRequest;
    private SimulationResponseDTO mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = new SimulationRequestDTO(
                "CUST-12345",
                new BigDecimal("150000.00"),
                new BigDecimal("1200.50"),
                new BigDecimal("200000.00"),
                240,
                "MORTGAGE",
                new BigDecimal("5000.00"),
                720
        );

        mockResponse = SimulationResponseDTO.builder()
                .simulationId("SIM-123456")
                .customerId("CUST-12345")
                .status("CALCULATED")
                .newLoanAmount(new BigDecimal("200000.00"))
                .newMonthlyPayment(new BigDecimal("1150.00"))
                .newInterestRate(new BigDecimal("4.5"))
                .termMonths(240)
                .totalInterest(new BigDecimal("76000.00"))
                .totalAmount(new BigDecimal("276000.00"))
                .monthlySavings(new BigDecimal("50.50"))
                .totalSavings(new BigDecimal("12120.00"))
                .approvalProbability(new BigDecimal("0.85"))
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .loanType("MORTGAGE")
                .build();
    }

    @Test
    @DisplayName("Should return health status successfully")
    void shouldReturnHealthStatus() {
        webTestClient
                .get()
                .uri("/api/simulation/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Simulation service is running");
    }

    @Test
    @DisplayName("Should calculate simulation successfully with valid request")
    void shouldCalculateSimulationSuccessfully() {
        // Given
        when(simulationService.calculateSimulation(any(SimulationRequestDTO.class)))
                .thenReturn(Mono.just(mockResponse));

        // When & Then
        webTestClient
                .post()
                .uri("/api/simulation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SimulationResponseDTO.class)
                .value(response -> {
                    assert response.getSimulationId().equals("SIM-123456");
                    assert response.getCustomerId().equals("CUST-12345");
                    assert response.getStatus().equals("CALCULATED");
                    assert response.getNewLoanAmount().compareTo(new BigDecimal("200000.00")) == 0;
                });
    }

    @Test
    @DisplayName("Should return 400 when customer ID is blank")
    void shouldReturn400WhenCustomerIdIsBlank() {
        // Given
        validRequest.setCustomerId("");

        // When & Then
        webTestClient
                .post()
                .uri("/api/simulation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should return 400 when loan amount is below minimum")
    void shouldReturn400WhenLoanAmountBelowMinimum() {
        // Given
        validRequest.setCurrentLoanAmount(new BigDecimal("500.00"));

        // When & Then
        webTestClient
                .post()
                .uri("/api/simulation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should return 400 when loan type is invalid")
    void shouldReturn400WhenLoanTypeIsInvalid() {
        // Given
        validRequest.setLoanType("INVALID_TYPE");

        // When & Then
        webTestClient
                .post()
                .uri("/api/simulation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should get simulation by ID successfully")
    void shouldGetSimulationByIdSuccessfully() {
        // Given
        String simulationId = "SIM-123456";
        when(simulationService.getSimulation(anyString()))
                .thenReturn(Mono.just(mockResponse));

        // When & Then
        webTestClient
                .get()
                .uri("/api/simulation/{simulationId}", simulationId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SimulationResponseDTO.class)
                .value(response -> {
                    assert response.getSimulationId().equals("SIM-123456");
                    assert response.getCustomerId().equals("CUST-12345");
                });
    }

    @Test
    @DisplayName("Should return 500 when service throws exception during calculation")
    void shouldReturn500WhenServiceThrowsException() {
        // Given
        when(simulationService.calculateSimulation(any(SimulationRequestDTO.class)))
                .thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        webTestClient
                .post()
                .uri("/api/simulation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Should return 404 when simulation not found")
    void shouldReturn404WhenSimulationNotFound() {
        // Given
        String simulationId = "NON-EXISTENT";
        when(simulationService.getSimulation(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Simulation not found")));

        // When & Then
        webTestClient
                .get()
                .uri("/api/simulation/{simulationId}", simulationId)
                .exchange()
                .expectStatus().isNotFound();
    }
}
