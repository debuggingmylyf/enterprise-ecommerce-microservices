package com.ecommerce.common.exception;

/**
 * Thrown when an attempt to register or create a user fails because the
 * user already exists (e.g. duplicate email address).
 *
 * <p>Maps to HTTP {@code 409 Conflict} in
 * {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}.
 */
public class UserAlreadyExistsException extends BaseException {

    /**
     * Constructs a {@code UserAlreadyExistsException} with a service-local code
     * (e.g. {@code "USER_ALREADY_EXISTS"}) surfaced in the error response.
     *
     * @param localErrorCode service-specific code
     * @param message        human-readable description
     */
    public UserAlreadyExistsException(final String localErrorCode, final String message) {
        super(localErrorCode, message, ErrorCode.DATA_INTEGRITY_VIOLATION);
    }

    /**
     * Constructs a {@code UserAlreadyExistsException} using the cross-cutting
     * {@link ErrorCode#DATA_INTEGRITY_VIOLATION} code.
     *
     * @param message human-readable description
     */
    public UserAlreadyExistsException(final String message) {
        super(ErrorCode.DATA_INTEGRITY_VIOLATION, message);
    }
}
