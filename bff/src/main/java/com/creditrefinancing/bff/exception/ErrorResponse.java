package com.creditrefinancing.bff.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@Schema(description = "Standard error response structure")
public class ErrorResponse {
    
    @JsonProperty("error_id")
    @Schema(description = "Unique error identifier for tracking", example = "550e8400-e29b-41d4-a716-446655440000")
    private String errorId;
    
    @JsonProperty("timestamp")
    @Schema(description = "Error occurrence timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
    
    @JsonProperty("status")
    @Schema(description = "HTTP status code", example = "400")
    private Integer status;
    
    @JsonProperty("error")
    @Schema(description = "Error type/category", example = "VALIDATION_FAILED")
    private String error;
    
    @JsonProperty("message")
    @Schema(description = "Human readable error message", example = "Request validation failed")
    private String message;
    
    @JsonProperty("details")
    @Schema(description = "Additional error details", example = "Customer ID is required")
    private String details;
    
    @JsonProperty("path")
    @Schema(description = "API path that caused the error", example = "/api/simulation/calculate")
    private String path;
    
    @JsonProperty("field_errors")
    @Schema(description = "Field-specific validation errors")
    private Map<String, String> fieldErrors;
}
