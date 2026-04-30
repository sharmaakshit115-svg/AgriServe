package com.agriserve.exception;

import com.agriserve.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralised exception handling for all REST controllers.
 * All exceptions return a consistent {@link ApiResponse} envelope.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // ─── Domain Exceptions ────────────────────────────────────────────────────

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
                        ResourceNotFoundException ex, WebRequest request) {
                log.warn("Resource not found: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Void>> handleBusinessException(
                        BusinessException ex, WebRequest request) {
                log.warn("Business rule violation: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(
                        DuplicateResourceException ex, WebRequest request) {
                log.warn("Duplicate resource: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        // ─── Validation Exceptions ────────────────────────────────────────────────

        /**
         * Handles @Valid annotation failures (e.g., blank field, invalid email).
         * Returns per-field error messages.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String message = error.getDefaultMessage();
                        errors.put(fieldName, message);
                });
                log.warn("Validation failed: {}", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.<Map<String, String>>builder()
                                                .success(false)
                                                .message("Validation failed")
                                                .data(errors)
                                                .build());
        }

        /**
         * Handles invalid enum values passed as @RequestParam or @PathVariable.
         * Example: GET /farmers?status=INVALID_VALUE
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Map<String, Object>>> handleMethodArgumentTypeMismatch(
                        MethodArgumentTypeMismatchException ex) {
                Class<?> requiredType = ex.getRequiredType();
                Map<String, Object> details = new HashMap<>();
                details.put("parameter", ex.getName());
                details.put("rejectedValue", String.valueOf(ex.getValue()));

                String message;
                if (requiredType != null && requiredType.isEnum()) {
                        List<String> allowedValues = Arrays.stream(requiredType.getEnumConstants())
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                        details.put("allowedValues", allowedValues);
                        message = String.format("Invalid value '%s' for parameter '%s'.",
                                        ex.getValue(), ex.getName());
                } else {
                        message = String.format("Invalid value '%s' for parameter '%s'",
                                        ex.getValue(), ex.getName());
                }

                log.warn("Type mismatch: {}", message);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.<Map<String, Object>>builder()
                                                .success(false)
                                                .message(message)
                                                .data(details)
                                                .build());
        }

        /**
         * Handles invalid enum values in JSON request body.
         * Example: POST /farmers with { "status": "INVALID_VALUE" }
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Map<String, Object>>> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException ex) {
                Map<String, Object> details = new HashMap<>();
                String message;

                Throwable cause = ex.getCause();
                if (cause instanceof InvalidFormatException ife && ife.getTargetType() != null
                                && ife.getTargetType().isEnum()) {
                        List<String> allowedValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                                        .map(Object::toString)
                                        .collect(Collectors.toList());
                        String fieldName = ife.getPath().isEmpty() ? "unknown"
                                        : ife.getPath().get(ife.getPath().size() - 1).getFieldName();
                        details.put("field", fieldName);
                        details.put("rejectedValue", String.valueOf(ife.getValue()));
                        details.put("allowedValues", allowedValues);
                        message = String.format("Invalid value '%s' for field '%s'. Allowed values: %s",
                                        ife.getValue(), fieldName, allowedValues);
                } else {
                        message = "Malformed JSON request body. Please check the request payload.";
                }

                log.warn("Unreadable HTTP message: {}", message);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.<Map<String, Object>>builder()
                                                .success(false)
                                                .message(message)
                                                .data(details)
                                                .build());
        }

        // ─── Security Exceptions ──────────────────────────────────────────────────

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
                log.warn("Access denied: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error(
                                                "Access denied: you do not have permission to perform this action"));
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error("Invalid email or password"));
        }

        // ─── Database Exceptions ──────────────────────────────────────────────────

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex) {
                log.error("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(
                                                "Data conflict: a record with the same unique value already exists"));
        }

        // ─── Fallback ─────────────────────────────────────────────────────────────

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, WebRequest request) {
                log.error("Unexpected error at {}: {}", request.getDescription(false), ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
        }
}
