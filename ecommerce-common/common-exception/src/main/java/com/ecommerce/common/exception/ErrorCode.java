package com.ecommerce.common.exception;

/**
 * Cross-cutting error codes shared by all microservices.
 *
 * <p><strong>Design rule:</strong> Only codes that can appear in <em>any</em> service
 * belong here. Service-specific codes (e.g. {@code PRODUCT_NOT_FOUND}, {@code TOKEN_EXPIRED})
 * live in the service's own {@code ErrorCode} enum and are passed as a {@code String}
 * to {@link com.ecommerce.common.response.ApiErrorResponse}.
 */
public enum ErrorCode {

    // ------------------------------------------------------------------
    // Generic / Infrastructure
    // ------------------------------------------------------------------

    /** An unexpected server-side error occurred. */
    INTERNAL_SERVER_ERROR,

    /** The requested resource could not be found. */
    RESOURCE_NOT_FOUND,

    /** One or more request fields failed bean-validation constraints. */
    VALIDATION_ERROR,

    /** The request conflicts with a unique database constraint. */
    DATA_INTEGRITY_VIOLATION,

    // ------------------------------------------------------------------
    // Business rules (cross-cutting)
    // ------------------------------------------------------------------

    /** A domain business rule was violated. */
    BUSINESS_RULE_VIOLATION,

    // ------------------------------------------------------------------
    // Security / Auth (cross-cutting — e.g. every service may need 403)
    // ------------------------------------------------------------------

    /** The caller does not have permission to perform the requested action. */
    ACCESS_DENIED,

    /** The provided authentication credentials are invalid or missing. */
    UNAUTHORIZED
}
