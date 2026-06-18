package com.ecommerce.product.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ApiErrorResponse {

    private String errorCode;

    private String message;

    private Integer status;

    private LocalDateTime timestamp;

    private List<FieldErrorResponse> errors;
}
