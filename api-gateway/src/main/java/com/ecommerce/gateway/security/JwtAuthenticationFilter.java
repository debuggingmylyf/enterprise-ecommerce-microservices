package com.ecommerce.gateway.security;

import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(...) {

        String authHeader =
                request.getHeader("Authorization");

        if(authHeader == null){
            filterChain.doFilter(request,response);
            return;
        }

        String token =
                authHeader.substring(7);

        if(jwtService.isValid(token)){

            String email =
                    jwtService.extractUsername(token);

            String role =
                    jwtService.extractRole(token);

            // Add headers
            request.setAttribute("userEmail", email);
            request.setAttribute("userRole", role);
        }

        filterChain.doFilter(request,response);
    }
}