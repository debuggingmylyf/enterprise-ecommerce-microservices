package com.ecommerce.product.exception;

import com.ecommerce.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Product-service exception handler.
 *
 * <p>Extends the shared {@link com.ecommerce.common.exception.handler.GlobalExceptionHandler}
 * which already handles:
 * <ul>
 *   <li>{@link com.ecommerce.common.exception.ResourceNotFoundException} → 404</li>
 *   <li>{@link com.ecommerce.common.exception.BusinessException} → 422</li>
 *   <li>{@link com.ecommerce.common.exception.AccessDeniedException} → 403</li>
 *   <li>{@link com.ecommerce.common.exception.InvalidTokenException} → 401</li>
 *   <li>{@link com.ecommerce.common.exception.TokenExpiredException} → 401</li>
 *   <li>{@link com.ecommerce.common.exception.UserAlreadyExistsException} → 409</li>
 *   <li>{@link org.springframework.web.bind.MethodArgumentNotValidException} → 400</li>
 *   <li>{@link org.springframework.dao.DataIntegrityViolationException} → 409</li>
 *   <li>{@link Exception} → 500</li>
 * </ul>
 *
 * <p>This subclass adds product-specific handlers if any additional product
 * service exceptions need custom responses beyond what the common handler provides.
 */
@RestControllerAdvice
public class GlobalExceptionHandler
        extends com.ecommerce.common.exception.handler.GlobalExceptionHandler {

    // The common handler covers all standard cases. Add product-specific
    // exception handlers below if needed in the future.

    // Example:
    // @ExceptionHandler(SomeProductSpecificException.class)
    // public ResponseEntity<ApiErrorResponse> handleProductSpecific(
    //         SomeProductSpecificException ex, HttpServletRequest request) { ... }
}
