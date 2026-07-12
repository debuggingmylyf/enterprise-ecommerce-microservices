package com.ecommerce.common.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a controller method parameter to the currently authenticated principal.
 *
 * <p>Intended for use with a {@code HandlerMethodArgumentResolver} implemented in
 * each service that reads the principal from the Spring Security {@code SecurityContext}
 * or from a forwarded request header (e.g. {@code X-User-Id} set by the API Gateway).
 *
 * <p>Example:
 * <pre>{@code
 * @GetMapping("/me")
 * public ResponseEntity<UserResponse> getMe(@CurrentUser AuthenticatedUser user) {
 *     return ResponseEntity.ok(userService.findById(user.getId()));
 * }
 * }</pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
