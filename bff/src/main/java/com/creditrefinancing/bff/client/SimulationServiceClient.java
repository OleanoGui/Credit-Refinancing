package com.creditrefinancing.bff.client;

import com.creditrefinancing.bff.dto.SimulationRequestDTO;
import com.creditrefinancing.bff.dto.SimulationResponseDTO;
import com.creditrefinancing.bff.exception.SimulationNotFoundException;
import com.creditrefinancing.bff.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimulationServiceClient {

    @Qualifier("simulationWebClient")
    private final WebClient simulationWebClient;

    /**
     * Calculate simulation by calling the simulation microservice
     */
    public Mono<SimulationResponseDTO> calculateSimulation(SimulationRequestDTO request) {
        log.info("Calling simulation service to calculate simulation for customer: {}", request.getCustomerId());
        
        return simulationWebClient
                .post()
                .uri("/simulations/calculate")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, 
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .map(body -> new BusinessRuleException("Invalid simulation request", body)))
                .onStatus(HttpStatus.UNPROCESSABLE_ENTITY::equals,
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .map(body -> new BusinessRuleException("Simulation business rule violation", body)))
                .bodyToMono(SimulationResponseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                    .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest)))
                .doOnSuccess(response -> log.info("Successfully calculated simulation: {}", response.getSimulationId()))
                .doOnError(error -> log.error("Error calculating simulation for customer: {}", request.getCustomerId(), error))
                .onErrorMap(WebClientResponseException.class, this::mapWebClientException);
    }

    /**
     * Get simulation by ID from the simulation microservice
     */
    public Mono<SimulationResponseDTO> getSimulation(String simulationId) {
        log.info("Calling simulation service to get simulation: {}", simulationId);
        
        return simulationWebClient
                .get()
                .uri("/simulations/{simulationId}", simulationId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    clientResponse -> Mono.error(new SimulationNotFoundException(simulationId)))
                .bodyToMono(SimulationResponseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                    .filter(throwable -> !(throwable instanceof WebClientResponseException.NotFound)))
                .doOnSuccess(response -> log.info("Successfully retrieved simulation: {}", simulationId))
                .doOnError(error -> log.error("Error retrieving simulation: {}", simulationId, error))
                .onErrorMap(WebClientResponseException.class, this::mapWebClientException);
    }

    /**
     * Update simulation status
     */
    public Mono<SimulationResponseDTO> updateSimulationStatus(String simulationId, String status) {
        log.info("Calling simulation service to update simulation {} status to: {}", simulationId, status);
        
        return simulationWebClient
                .patch()
                .uri("/simulations/{simulationId}/status", simulationId)
                .bodyValue(new StatusUpdateRequest(status))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    clientResponse -> Mono.error(new SimulationNotFoundException(simulationId)))
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .map(body -> new BusinessRuleException("Invalid status update", body)))
                .bodyToMono(SimulationResponseDTO.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                .doOnSuccess(response -> log.info("Successfully updated simulation {} status", simulationId))
                .doOnError(error -> log.error("Error updating simulation {} status", simulationId, error))
                .onErrorMap(WebClientResponseException.class, this::mapWebClientException);
    }

    /**
     * Check simulation service health
     */
    public Mono<String> checkHealth() {
        log.debug("Checking simulation service health");
        
        return simulationWebClient
                .get()
                .uri("/health")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(response -> log.debug("Simulation service health check successful"))
                .doOnError(error -> log.warn("Simulation service health check failed", error))
                .onErrorReturn("Simulation service unavailable");
    }

    /**
     * Maps WebClient exceptions to domain exceptions
     */
    private Throwable mapWebClientException(WebClientResponseException ex) {
        return switch (ex.getStatusCode().value()) {
            case 400 -> new BusinessRuleException("Bad request to simulation service", ex.getResponseBodyAsString());
            case 404 -> new SimulationNotFoundException("Simulation not found in service");
            case 422 -> new BusinessRuleException("Business rule violation in simulation service", ex.getResponseBodyAsString());
            case 500, 502, 503, 504 -> new RuntimeException("Simulation service unavailable", ex);
            default -> new RuntimeException("Unexpected error from simulation service", ex);
        };
    }

    // Inner class for status update requests
    private record StatusUpdateRequest(String status) {}
}
