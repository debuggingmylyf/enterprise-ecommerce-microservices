package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.AuthResponse;
import com.ecommerce.auth.entity.RefreshToken;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.exception.InvalidTokenException;
import com.ecommerce.auth.exception.TokenExpiredException;
import com.ecommerce.auth.repository.RefreshTokenRepository;
import com.ecommerce.auth.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String TOKEN_TYPE = "Bearer";

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(jwtService.generateRefreshToken(user.getEmail()))
                .expiryDate(jwtService.getRefreshTokenExpiryDate())
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public AuthResponse rotateRefreshToken(String token) {
        RefreshToken existingToken = getValidStoredToken(token);
        validateRefreshJwt(token, existingToken.getUser().getEmail());

        revoke(existingToken);
        RefreshToken rotatedToken = createRefreshToken(existingToken.getUser());
        String accessToken = jwtService.generateAccessToken(existingToken.getUser().getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rotatedToken.getToken())
                .tokenType(TOKEN_TYPE)
                .build();
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token is not recognized"));

        revoke(refreshToken);
    }

    @Transactional
    public int revokeAllActiveTokensForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("User is not recognized"));

        return refreshTokenRepository.revokeAllActiveTokensByUser(user, LocalDateTime.now());
    }

    private RefreshToken getValidStoredToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token is not recognized"));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            revoke(refreshToken);
            throw new TokenExpiredException("Refresh token has expired");
        }

        return refreshToken;
    }

    private void validateRefreshJwt(String token, String email) {
        try {
            if (!jwtService.isRefreshToken(token) || !jwtService.isTokenValid(token, email)) {
                throw new InvalidTokenException("Invalid refresh token");
            }
        } catch (ExpiredJwtException ex) {
            throw new TokenExpiredException("Refresh token has expired");
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidTokenException("Invalid refresh token");
        }
    }

    private void revoke(RefreshToken refreshToken) {
        if (!refreshToken.isRevoked()) {
            refreshToken.setRevoked(true);
            refreshToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);
        }
    }
}
