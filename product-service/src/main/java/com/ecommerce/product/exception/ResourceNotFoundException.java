package com.ecommerce.product.exception;

/**
 * Thrown when a requested domain entity (Product, Category, etc.) cannot be
 * located in the data store.
 *
 * <p>Maps to HTTP {@code 404 Not Found} in {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceNotFoundException} with the specified detail message.
     *
     * @param message human-readable description of the missing resource
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ResourceNotFoundException} with a detail message and root cause.
     *
     * @param message human-readable description of the missing resource
     * @param cause   the underlying exception that triggered this one
     */
    public ResourceNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

