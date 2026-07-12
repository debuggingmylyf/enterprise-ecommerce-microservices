package com.ecommerce.common.exception;

/**
 * Thrown when a requested domain entity cannot be located in the data store.
 *
 * <p>Maps to HTTP {@code 404 Not Found} in
 * {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}.
 *
 * <p>Usage with a cross-cutting code:
 * <pre>{@code
 * throw new ResourceNotFoundException("Product with id " + id + " was not found");
 * }</pre>
 *
 * <p>Usage with a service-specific code:
 * <pre>{@code
 * throw new ResourceNotFoundException("PRODUCT_NOT_FOUND", "Product " + id + " not found");
 * }</pre>
 */
public class ResourceNotFoundException extends BaseException {

    /**
     * Constructs a {@code ResourceNotFoundException} using the cross-cutting
     * {@link ErrorCode#RESOURCE_NOT_FOUND} code.
     *
     * @param message human-readable description of the missing resource
     */
    public ResourceNotFoundException(final String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

    /**
     * Constructs a {@code ResourceNotFoundException} with a service-local error
     * code for finer-grained machine-readable reporting.
     *
     * @param localErrorCode service-specific code (e.g. "PRODUCT_NOT_FOUND")
     * @param message        human-readable description
     */
    public ResourceNotFoundException(final String localErrorCode, final String message) {
        super(localErrorCode, message, ErrorCode.RESOURCE_NOT_FOUND);
    }

    /**
     * Constructs a {@code ResourceNotFoundException} with a detail message and root cause.
     *
     * @param message human-readable description of the missing resource
     * @param cause   the underlying exception
     */
    public ResourceNotFoundException(final String message, final Throwable cause) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message, cause);
    }
}
