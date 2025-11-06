package com.creditrefinancing.bff.exception;

public class SimulationNotFoundException extends RuntimeException {
    
    public SimulationNotFoundException(String simulationId) {
        super("Simulation not found with ID: " + simulationId);
    }
    
    public SimulationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
