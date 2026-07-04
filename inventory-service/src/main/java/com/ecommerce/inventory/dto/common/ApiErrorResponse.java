package com.ecommerce.inventory.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardised error response payload returned for all error HTTP responses.
 *
 * <p>The structure mirrors the product-service API contract to ensure a
 * consistent error format across the entire platform.
 */
@Getter
@Setter
@Builder
public class ApiErrorResponse {

    /** Machine-readable error code (see {@link com.ecommerce.inventory.exception.ErrorCode}). */
    private String errorCode;

    /** Human-readable summary of the error. */
    private String message;

    /** HTTP status code (e.g. 400, 404, 422). */
    private Integer status;

    /** Server timestamp when the error occurred. */
    private LocalDateTime timestamp;

    /** Per-field validation errors; {@code null} when not applicable. */
    private List<FieldErrorResponse> errors;
}
