package com.ecommerce.inventory.exception;

/**
 * Domain-specific error codes for the Inventory Service.
 * Used by {@link GlobalExceptionHandler} to populate structured API error responses.
 */
public enum ErrorCode {

    /** A requested inventory record could not be located. */
    INVENTORY_NOT_FOUND,

    /** Available stock is insufficient to fulfil the requested reservation. */
    INSUFFICIENT_STOCK,

    /** An inventory record for this product-warehouse combination already exists. */
    DUPLICATE_INVENTORY,

    /** The supplied quantity is invalid (e.g. zero or negative). */
    INVALID_QUANTITY,

    /** One or more request fields failed bean-validation constraints. */
    VALIDATION_ERROR,

    /** The request conflicts with a unique database constraint. */
    DATA_INTEGRITY_VIOLATION,

    /** An unexpected server-side error occurred. */
    INTERNAL_SERVER_ERROR
}
