package com.ecommerce.inventory.exception;

/**
 * Thrown when a domain business rule is violated (e.g. insufficient stock,
 * invalid quantity, duplicate inventory record).
 *
 * <p>Maps to HTTP {@code 422 Unprocessable Entity} in {@link GlobalExceptionHandler}.
 */
public class BusinessException extends RuntimeException {

    /** Structured error code for machine-readable API responses. */
    private final ErrorCode errorCode;

    /**
     * Constructs a new {@code BusinessException} with an error code and detail message.
     *
     * @param errorCode structured {@link ErrorCode} identifying the violation
     * @param message   human-readable description of the rule that was violated
     */
    public BusinessException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Returns the structured {@link ErrorCode} associated with this exception.
     *
     * @return the error code; never {@code null}
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
