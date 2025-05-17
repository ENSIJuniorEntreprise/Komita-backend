package com.yt.backend.exception;

import java.util.Map;
import java.util.HashMap;

public class ValidationException extends RuntimeException {
    private final Map<String, String> errors;
    
    public ValidationException(String message) {
        super(message);
        this.errors = new HashMap<>();
    }
    
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
    
    public void addError(String field, String message) {
        this.errors.put(field, message);
    }
}