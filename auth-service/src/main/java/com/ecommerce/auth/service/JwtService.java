package com.ecommerce.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.access-expiration}")
    private long jwtAccessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    public String generateToken(String email){
        // Backwards-compatible: default to access token
        return generateAccessToken(email);
    }

    public String generateAccessToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateRefreshToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String extractUsername(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        final String username = extractUsername(token);
        return username.equals(email);
    }

    // Helper to get the refresh token expiry as a LocalDateTime for storing in DB
    public LocalDateTime getRefreshTokenExpiryDate(){
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis() + jwtRefreshExpiration);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}
