package com.ecommerce.gateway.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        return PUBLIC_ENDPOINTS.stream()
                .noneMatch(publicPath -> path.equals(publicPath));
    };

    public boolean hasRequiredRole(ServerHttpRequest request, String userRole) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (path.contains("/api/v1/products")) {
            if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
                return "ROLE_ADMIN".equals(userRole) || "ROLE_SELLER".equals(userRole);
            }
            if (method == HttpMethod.DELETE) {
                return "ROLE_ADMIN".equals(userRole);
            }
            return true;
        }

        if (path.contains("/api/v1/categories")) {
            if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE) {
                return "ROLE_ADMIN".equals(userRole);
            }
            return true;
        }

        if (path.contains("/api/v1/internal/orders")) {
            return "ROLE_INTERNAL_SERVICE".equals(userRole) || "INTERNAL_SERVICE".equals(userRole);
        }

        if (path.contains("/api/v1/orders")) {
            // GET /api/v1/orders (admin fetch all)
            if (method == HttpMethod.GET && "/api/v1/orders".equals(path)) {
                return "ROLE_ADMIN".equals(userRole);
            }
            // PATCH /api/v1/orders/{id}/status
            if (method == HttpMethod.PATCH && path.endsWith("/status")) {
                return "ROLE_ADMIN".equals(userRole);
            }
            return true;
        }

        if (path.contains("/api/v1/inventory")) {
            if (path.contains("/internal/provision") || path.contains("/reserve") || path.contains("/release") || path.contains("/confirm")) {
                return "ROLE_INTERNAL_SERVICE".equals(userRole) || "INTERNAL_SERVICE".equals(userRole);
            }
            // Admin operations
            if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
                return "ROLE_ADMIN".equals(userRole);
            }
            return true;
        }

        return true;
    }
}
