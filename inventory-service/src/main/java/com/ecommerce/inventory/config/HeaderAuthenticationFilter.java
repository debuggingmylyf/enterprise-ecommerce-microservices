package com.ecommerce.inventory.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Extracts authenticated user context from gateway-forwarded HTTP headers.
 *
 * <p>The API Gateway validates JWT tokens and forwards user identity as
 * {@code X-User-Email} and {@code X-User-Role} headers. This filter reads
 * those headers and stores them as request attributes for downstream use
 * (e.g. populating audit fields).
 *
 * <p>This service trusts all inbound traffic routed through the gateway and
 * does not perform its own token validation, consistent with the product-service model.
 */
@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HeaderAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain)
            throws IOException, ServletException {

        final String email = request.getHeader("X-User-Email");
        final String role  = request.getHeader("X-User-Role");

        if (email != null && role != null) {
            log.debug("Authenticated request from gateway – user: {}, role: {}", email, role);
            request.setAttribute("userEmail", email);
            request.setAttribute("userRole", role);
        }

        filterChain.doFilter(request, response);
    }
}
