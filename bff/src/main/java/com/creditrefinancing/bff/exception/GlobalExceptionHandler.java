package com.creditrefinancing.bff.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import org.springframework.core.codec.DecodingException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(
            WebExchangeBindException ex) {
        
        String errorId = UUID.randomUUID().toString();
        log.error("Validation error [{}]: {}", errorId, ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_FAILED")
                .message("Request validation failed")
                .path(ex.getMethodParameter() != null && ex.getMethodParameter().getMethod() != null 
                        ? ex.getMethodParameter().getMethod().getName() : "unknown")
                .fieldErrors(fieldErrors)
                .build();
        
        return Mono.just(ResponseEntity.badRequest().body(response));
    }    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResponseStatusException(
            ResponseStatusException ex) {
        
        String errorId = UUID.randomUUID().toString();
        log.warn("Response status error [{}]: {} - {}", errorId, ex.getStatusCode(), ex.getReason());
        
        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(ex.getStatusCode().value())
                .error(ex.getStatusCode().toString())
                .message(ex.getReason() != null ? ex.getReason() : "Request failed")
                .build();
        
        return Mono.just(new ResponseEntity<>(response, ex.getStatusCode()));
    }

    @ExceptionHandler(DecodingException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDecodingException(
            DecodingException ex) {
        
        String errorId = UUID.randomUUID().toString();
        log.error("JSON parsing error [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_JSON")
                .message("Invalid JSON format in request body")
                .details(ex.getCause() != null ? ex.getCause().getMessage() : null)
                .build();
        
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    @ExceptionHandler(TimeoutException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTimeoutException(
            TimeoutException ex) {
        
        String errorId = UUID.randomUUID().toString();
        log.error("Timeout error [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.REQUEST_TIMEOUT.value())
                .error("REQUEST_TIMEOUT")
                .message("Request timed out - please try again")
                .build();
        
        return Mono.just(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        String errorId = UUID.randomUUID().toString();
        log.warn("Invalid argument [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_ARGUMENT")
                .message("Invalid request parameter")
                .details(ex.getMessage())
                .build();
        
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    @ExceptionHandler(SimulationNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleSimulationNotFoundException(
            SimulationNotFoundException ex) {
        
        String errorId = UUID.randomUUID().toString();
        log.warn("Simulation not found [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("SIMULATION_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        
        return Mono.just(new ResponseEntity<>(response, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusinessRuleException(
            BusinessRuleException ex) {
        
        String errorId = UUID.randomUUID().toString();
        log.warn("Business rule violation [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error("BUSINESS_RULE_VIOLATED")
                .message(ex.getMessage())
                .details(ex.getDetails())
                .build();
        
        return Mono.just(new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleRuntimeException(
            RuntimeException ex) {
        
        String errorId = UUID.randomUUID().toString();
        log.error("Runtime error [{}]: {}", errorId, ex.getMessage(), ex);
        
        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("An internal error occurred")
                .build();
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex) {
        
        String errorId = UUID.randomUUID().toString();
        log.error("Unexpected error [{}]: {}", errorId, ex.getMessage(), ex);
        
        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("UNEXPECTED_ERROR")
                .message("An unexpected error occurred")
                .build();
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}
