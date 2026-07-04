package com.ecommerce.inventory.exception;

/**
 * Thrown when a requested inventory resource cannot be found in the data store.
 *
 * <p>Maps to HTTP {@code 404 Not Found} in {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceNotFoundException} with the given detail message.
     *
     * @param message human-readable description of which resource was not found
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
