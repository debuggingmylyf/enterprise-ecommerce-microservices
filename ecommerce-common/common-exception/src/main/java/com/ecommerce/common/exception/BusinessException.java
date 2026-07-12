package com.ecommerce.common.exception;

/**
 * Thrown when a domain business rule is violated
 * (e.g. duplicate SKU, invalid price range, deactivated product operation).
 *
 * <p>Maps to HTTP {@code 422 Unprocessable Entity} in
 * {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}.
 *
 * <p>Usage with a cross-cutting code:
 * <pre>{@code
 * throw new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION, "Price range is invalid");
 * }</pre>
 *
 * <p>Usage with a service-specific code (keeps service enum local):
 * <pre>{@code
 * throw new BusinessException("DUPLICATE_SKU", "SKU 'ABC-123' already exists");
 * }</pre>
 */
public class BusinessException extends BaseException {

    /**
     * Constructs a {@code BusinessException} with a cross-cutting {@link ErrorCode}.
     *
     * @param errorCode structured error code
     * @param message   human-readable description of the violated rule
     */
    public BusinessException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    /**
     * Constructs a {@code BusinessException} with a service-local error code string,
     * using {@link ErrorCode#BUSINESS_RULE_VIOLATION} as the HTTP-status fallback.
     *
     * @param localErrorCode service-specific code name (e.g. "DUPLICATE_SKU")
     * @param message        human-readable description
     */
    public BusinessException(final String localErrorCode, final String message) {
        super(localErrorCode, message, ErrorCode.BUSINESS_RULE_VIOLATION);
    }
}
