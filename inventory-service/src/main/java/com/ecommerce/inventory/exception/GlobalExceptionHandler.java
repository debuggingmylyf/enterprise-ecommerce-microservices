package com.ecommerce.inventory.exception;

import com.ecommerce.inventory.dto.common.ApiErrorResponse;
import com.ecommerce.inventory.dto.common.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Centralized exception handler for all controllers in the Inventory Service.
 *
 * <p>Maps domain exceptions to structured {@link ApiErrorResponse} payloads and
 * appropriate HTTP status codes, ensuring a consistent error contract for API consumers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link ResourceNotFoundException} when an inventory record cannot be found.
     *
     * @param ex      the exception carrying the detail message
     * @param request the current HTTP request (used to populate the {@code path} log field)
     * @return {@code 404 Not Found} with a structured error body
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            final ResourceNotFoundException ex,
            final HttpServletRequest request) {

        log.warn("Resource not found [path={}]: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(
                HttpStatus.NOT_FOUND,
                ErrorCode.INVENTORY_NOT_FOUND.name(),
                ex.getMessage(),
                null
        ));
    }

    /**
     * Handles {@link BusinessException} for domain rule violations such as insufficient stock.
     *
     * @param ex      the exception carrying the {@link ErrorCode} and detail message
     * @param request the current HTTP request
     * @return {@code 422 Unprocessable Entity} with a structured error body
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            final BusinessException ex,
            final HttpServletRequest request) {

        log.warn("Business rule violation [path={}, code={}]: {}",
                request.getRequestURI(), ex.getErrorCode(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(buildError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getErrorCode().name(),
                ex.getMessage(),
                null
        ));
    }

    /**
     * Handles bean-validation failures from {@code @Valid}-annotated request bodies.
     *
     * @param ex      the Spring MVC validation exception
     * @param request the current HTTP request
     * @return {@code 400 Bad Request} with per-field error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            final MethodArgumentNotValidException ex,
            final HttpServletRequest request) {

        log.warn("Validation failed [path={}]: {} field error(s)",
                request.getRequestURI(), ex.getBindingResult().getFieldErrorCount());

        final List<FieldErrorResponse> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> FieldErrorResponse.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();

        return ResponseEntity.badRequest().body(buildError(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR.name(),
                "Request validation failed",
                fieldErrors
        ));
    }

    /**
     * Handles database unique-constraint violations (e.g. duplicate product-warehouse pair).
     *
     * @param ex      the Spring Data exception
     * @param request the current HTTP request
     * @return {@code 409 Conflict} with a structured error body
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            final DataIntegrityViolationException ex,
            final HttpServletRequest request) {

        log.error("Data integrity violation [path={}]: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildError(
                HttpStatus.CONFLICT,
                ErrorCode.DATA_INTEGRITY_VIOLATION.name(),
                "Request conflicts with existing data",
                null
        ));
    }

    /**
     * Catch-all handler for any unhandled exception.
     *
     * @param ex      the exception
     * @param request the current HTTP request
     * @return {@code 500 Internal Server Error} with a generic error body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            final Exception ex,
            final HttpServletRequest request) {

        log.error("Unhandled exception [path={}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                "An unexpected error occurred. Please try again later.",
                null
        ));
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    private ApiErrorResponse buildError(
            final HttpStatus status,
            final String errorCode,
            final String message,
            final List<FieldErrorResponse> fieldErrors) {

        return ApiErrorResponse.builder()
                .status(status.value())
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .errors(fieldErrors)
                .build();
    }
}
