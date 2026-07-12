package com.ecommerce.common.exception;

/**
 * Thrown when an authentication token is syntactically invalid or tampered with.
 *
 * <p>Maps to HTTP {@code 401 Unauthorized} in
 * {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}.
 */
public class InvalidTokenException extends BaseException {

    /**
     * Constructs an {@code InvalidTokenException} with a service-local code
     * (e.g. {@code "INVALID_TOKEN"}) surfaced in the error response.
     *
     * @param localErrorCode service-specific code
     * @param message        human-readable description
     */
    public InvalidTokenException(final String localErrorCode, final String message) {
        super(localErrorCode, message, ErrorCode.UNAUTHORIZED);
    }

    /**
     * Constructs an {@code InvalidTokenException} using the cross-cutting
     * {@link ErrorCode#UNAUTHORIZED} code.
     *
     * @param message human-readable description
     */
    public InvalidTokenException(final String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
