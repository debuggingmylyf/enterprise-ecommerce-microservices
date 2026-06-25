package com.ecommerce.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    @Value("${jwt.secret}")
    private String secretKey;

    // legacy single expiration property removed in favor of separate access/refresh expirations

    @Value("${jwt.access-expiration}")
    private long jwtAccessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    // Backwards-compatible convenience: generate an access token for the given email (no role)
    public String generateToken(String email) {
        return generateAccessToken(email, null);
    }

    // Generate an access token for the given email and role
    public String generateToken(String email, String role) {
        return generateAccessToken(email, role);
    }

    public String generateAccessToken(String email, String role) {
        return generateToken(email, role, ACCESS_TOKEN_TYPE, jwtAccessExpiration);
    }

    // Backwards-compatible convenience: generate a refresh token for the given email (no role)
    public String generateRefreshToken(String email) {
        return generateRefreshToken(email, null);
    }

    // Generate a refresh token for the given email and role
    public String generateRefreshToken(String email, String role) {
        return generateToken(email, role, REFRESH_TOKEN_TYPE, jwtRefreshExpiration);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isAccessToken(String token) {
        return ACCESS_TOKEN_TYPE.equals(extractTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return REFRESH_TOKEN_TYPE.equals(extractTokenType(token));
    }

    public boolean isTokenValid(String token, String email) {
        Claims claims = extractAllClaims(token);
        return email.equals(claims.getSubject()) && claims.getExpiration().after(new Date());
    }

    public LocalDateTime getRefreshTokenExpiryDate() {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis() + jwtRefreshExpiration);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    private String generateToken(String email, String role, String tokenType, long expiration) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .setId(UUID.randomUUID().toString());

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.claim(TOKEN_TYPE_CLAIM, tokenType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private String extractTokenType(String token) {
        return extractAllClaims(token).get(TOKEN_TYPE_CLAIM, String.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
