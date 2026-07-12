package com.ecommerce.common.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a single field-level validation error within an {@link ApiErrorResponse}.
 *
 * <p>Returned as a list inside {@code ApiErrorResponse.errors} when bean-validation
 * fails on a request body.
 */
@Getter
@Builder
public class FieldError {

    /** The name of the field that failed validation (e.g. {@code "price"}). */
    private final String field;

    /** The constraint-violation message for the field (e.g. {@code "must be positive"}). */
    private final String message;
}
