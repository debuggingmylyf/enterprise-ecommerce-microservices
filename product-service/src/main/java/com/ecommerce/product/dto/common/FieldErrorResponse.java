package com.ecommerce.product.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FieldErrorResponse {

    private String field;

    private String message;
}
