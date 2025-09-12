package com.reliaquest.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<APIException> handleEmployeeNotFound(EmployeeNotFoundException ex, WebRequest req) {
        log.warn("Not found: {}", ex.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status)
                .body(APIException.of(status, ex.getMessage(), req.getDescription(false)));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIException> handleBadRequest(BadRequestException ex, WebRequest req) {
        log.warn("Bad request: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(APIException.of(status, ex.getMessage(), req.getDescription(false)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIException> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(err -> err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        log.warn("Validation error: {}", message);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(APIException.of(status, message, req.getDescription(false)));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIException> handleConstraint(ConstraintViolationException ex, WebRequest req) {
        String message = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("Constraint violation");
        log.warn("Constraint violation: {}", message);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(APIException.of(status, message, req.getDescription(false)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIException> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest req) {
        String message = String.format("Invalid format for parameter '%s'. Expected: %s, but received: '%s'",
                ex.getName(),
                ex.getRequiredType().getSimpleName(),
                ex.getValue());
        log.warn("Type mismatch error: {}", message);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(APIException.of(status, message, req.getDescription(false)));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<APIException> handleDownstream(WebClientResponseException ex, WebRequest req) {
        log.error("Downstream API error {} {}: {}", ex.getStatusCode(), ex.getStatusText(), ex.getResponseBodyAsString());
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        return ResponseEntity.status(status)
                .body(APIException.of(status, "Upstream service error", req.getDescription(false)));
    }

    @ExceptionHandler({ ErrorResponseException.class, Exception.class })
    public ResponseEntity<APIException> handleAll(Exception ex, WebRequest req) {
        log.error("Unexpected error occoured", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(APIException.of(status, "Internal server error", req.getDescription(false)));
    }
}