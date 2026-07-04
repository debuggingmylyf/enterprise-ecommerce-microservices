package com.ecommerce.inventory.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a single field-level validation error within an {@link ApiErrorResponse}.
 */
@Getter
@Setter
@Builder
public class FieldErrorResponse {

    /** The name of the request field that failed validation. */
    private String field;

    /** The validation constraint message describing why the field is invalid. */
    private String message;
}
