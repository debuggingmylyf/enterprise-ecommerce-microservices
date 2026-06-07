package com.ecommerce.auth.repository;

import com.ecommerce.auth.entity.RefreshToken;
import com.ecommerce.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update RefreshToken token
               set token.revoked = true,
                   token.revokedAt = :revokedAt
             where token.user = :user
               and token.revoked = false
            """)
    int revokeAllActiveTokensByUser(@Param("user") User user, @Param("revokedAt") LocalDateTime revokedAt);
}
