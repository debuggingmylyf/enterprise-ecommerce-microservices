package com.ecommerce.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Unified error response envelope used by all microservices.
 *
 * <p>Replaces both the former {@code ApiErrorResponse} in product-service and
 * {@code ApiError} in auth-service with a single, consistent shape:
 *
 * <pre>{@code
 * {
 *   "status": 404,
 *   "errorCode": "RESOURCE_NOT_FOUND",
 *   "message": "Product with id … was not found",
 *   "path": "/api/v1/products/…",
 *   "timestamp": "2026-07-11T12:00:00",
 *   "errors": null
 * }
 * }</pre>
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    /** HTTP status code (e.g. 400, 404, 422). */
    private final int status;

    /**
     * Machine-readable error code from the local service {@code ErrorCode} enum
     * or from {@link com.ecommerce.common.exception.ErrorCode} for cross-cutting errors.
     */
    private final String errorCode;

    /** Human-readable description of the error. */
    private final Object message;

    /** The request URI that triggered the error. */
    private final String path;

    /** Timestamp at which the error occurred. */
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Per-field validation errors; populated only for {@code 400 Bad Request}
     * validation failures.
     */
    private final List<FieldError> errors;
}
