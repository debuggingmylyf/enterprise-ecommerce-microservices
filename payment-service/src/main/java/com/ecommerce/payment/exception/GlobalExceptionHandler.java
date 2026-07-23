package com.ecommerce.payment.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the Payment Service.
 *
 * <p>Extends the shared {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}
 * which automatically handles ResourceNotFoundException, BusinessException,
 * and MethodArgumentNotValidException with appropriate HTTP status codes.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler
        extends com.ecommerce.common.exception.handler.GlobalExceptionHandler {

    // Inherits standard handlers from the common library. Add payment-service specific
    // exception handlers here if required in the future.
}
