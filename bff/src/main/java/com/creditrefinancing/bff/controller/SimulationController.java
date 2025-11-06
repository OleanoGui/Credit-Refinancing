package com.creditrefinancing.bff.controller;

import com.creditrefinancing.bff.dto.SimulationRequestDTO;
import com.creditrefinancing.bff.dto.SimulationResponseDTO;
import com.creditrefinancing.bff.service.SimulationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/simulation")
@Tag(name = "Simulation", description = "Credit refinancing simulation endpoints")
@RequiredArgsConstructor
@Slf4j
public class SimulationController {

    private final SimulationService simulationService;

    @GetMapping("/health")
    @Operation(summary = "Health check for simulation service")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public Mono<ResponseEntity<String>> health() {
        return Mono.just(ResponseEntity.ok("Simulation service is running"));
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate credit refinancing simulation")
    @ApiResponse(responseCode = "200", description = "Simulation calculated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    public Mono<ResponseEntity<SimulationResponseDTO>> calculateSimulation(
            @Valid @RequestBody SimulationRequestDTO request) {
        
        log.info("Received simulation request for customer: {}", request.getCustomerId());
        
        return simulationService.calculateSimulation(request)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/{simulationId}")
    @Operation(summary = "Get simulation by ID")
    @ApiResponse(responseCode = "200", description = "Simulation found")
    @ApiResponse(responseCode = "404", description = "Simulation not found")
    public Mono<ResponseEntity<SimulationResponseDTO>> getSimulation(@PathVariable String simulationId) {
        log.info("Getting simulation: {}", simulationId);
        
        return simulationService.getSimulation(simulationId)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.notFound().build());
    }
}
