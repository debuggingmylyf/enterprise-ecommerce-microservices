package com.ecommerce.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.access-expiration}")
    private long jwtAccessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    public String generateToken(String email) {
        return generateAccessToken(email);
    }

    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN_TYPE, jwtAccessExpiration);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN_TYPE, jwtRefreshExpiration);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
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

    private String generateToken(String email, String tokenType, long expiration) {
        return Jwts.builder()
                .setSubject(email)
                .setId(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, tokenType)
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
