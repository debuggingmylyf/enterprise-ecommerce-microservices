package com.ecommerce.common.exception;

/**
 * Thrown when the caller attempts an action they are not permitted to perform.
 *
 * <p>Maps to HTTP {@code 403 Forbidden} in
 * {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}.
 */
public class AccessDeniedException extends BaseException {

    /**
     * Constructs an {@code AccessDeniedException} with the cross-cutting
     * {@link ErrorCode#ACCESS_DENIED} code.
     *
     * @param message human-readable description of the access violation
     */
    public AccessDeniedException(final String message) {
        super(ErrorCode.ACCESS_DENIED, message);
    }
}
