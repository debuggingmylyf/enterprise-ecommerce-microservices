package com.ecommerce.product.exception;

/**
 * Domain-specific error codes for the Product Service.
 * Used by {@link GlobalExceptionHandler} to populate structured API error responses.
 */
public enum ErrorCode {

    /** A requested product could not be located. */
    PRODUCT_NOT_FOUND,

    /** A requested category could not be located. */
    CATEGORY_NOT_FOUND,

    /** A product with the given SKU already exists. */
    DUPLICATE_SKU,

    /** A product with the derived slug already exists. */
    DUPLICATE_SLUG,

    /** A category with the given name already exists. */
    DUPLICATE_CATEGORY_NAME,

    /** The supplied price range is invalid (e.g. min &gt; max). */
    INVALID_PRICE_RANGE,

    /** One or more request fields failed bean-validation constraints. */
    VALIDATION_ERROR,

    /** The request conflicts with a unique database constraint. */
    DATA_INTEGRITY_VIOLATION,

    /** Inventory provisioning for a newly created product failed; manual provisioning required. */
    INVENTORY_PROVISIONING_FAILED,

    /** An unexpected server-side error occurred. */
    INTERNAL_SERVER_ERROR
}

