package com.labourconnect.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralizes error handling for the exceptions this application actually throws,
 * so every endpoint returns the same {"error": "..."} shape instead of a raw 500
 * or ad-hoc per-controller try/catch blocks. Deliberately does NOT add a catch-all
 * Exception handler - that would override Spring Boot's own correct default handling
 * of things like malformed JSON (400) or unsupported HTTP methods (405).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Thrown by MatchingService when a job/offer id doesn't exist - maps to 404
    // so callers see a clean "not found" instead of a raw 500.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        String message = e.getMessage() != null ? e.getMessage() : "Resource not found.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", message));
    }

    // Thrown by MatchingService when an offer has already been responded to -
    // maps to 400 since the request itself is well-formed, just no longer actionable.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        String message = e.getMessage() != null ? e.getMessage() : "Request could not be processed.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
    }

    // Thrown intentionally by controller/service code (e.g. an invalid Skill string)
    // when it already knows the right HTTP status to return.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException e) {
        String message = e.getReason() != null ? e.getReason() : "Request could not be processed.";
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", message));
    }

    // Triggered by @Valid failures on request DTOs (WorkerRequest, ClientRequest, JobRequest).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation failed");
        body.put("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Thrown when a unique constraint is violated, e.g. registering a worker or
    // client with a phone number that's already in use.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "A record with these unique values already exists (e.g. duplicate phone number)."));
    }
}