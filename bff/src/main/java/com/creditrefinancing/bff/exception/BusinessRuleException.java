package com.creditrefinancing.bff.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {
    
    private final String details;
    
    public BusinessRuleException(String message) {
        super(message);
        this.details = null;
    }
    
    public BusinessRuleException(String message, String details) {
        super(message);
        this.details = details;
    }
    
    public BusinessRuleException(String message, String details, Throwable cause) {
        super(message, cause);
        this.details = details;
    }
}
