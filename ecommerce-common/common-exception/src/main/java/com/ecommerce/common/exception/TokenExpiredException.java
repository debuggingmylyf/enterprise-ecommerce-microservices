package com.ecommerce.common.exception;

/**
 * Thrown when an authentication token has expired.
 *
 * <p>Maps to HTTP {@code 401 Unauthorized} in
 * {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}.
 */
public class TokenExpiredException extends BaseException {

    /**
     * Constructs a {@code TokenExpiredException} with a service-local code
     * (e.g. {@code "TOKEN_EXPIRED"}) surfaced in the error response.
     *
     * @param localErrorCode service-specific code
     * @param message        human-readable description
     */
    public TokenExpiredException(final String localErrorCode, final String message) {
        super(localErrorCode, message, ErrorCode.UNAUTHORIZED);
    }

    /**
     * Constructs a {@code TokenExpiredException} using the cross-cutting
     * {@link ErrorCode#UNAUTHORIZED} code.
     *
     * @param message human-readable description
     */
    public TokenExpiredException(final String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
