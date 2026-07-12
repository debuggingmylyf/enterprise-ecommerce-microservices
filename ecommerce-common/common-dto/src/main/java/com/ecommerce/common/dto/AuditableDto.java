package com.ecommerce.common.dto;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Base class for response DTOs that carry audit timestamps.
 *
 * <p>Extend this in any service-specific response DTO where the entity has
 * {@code createdAt} / {@code updatedAt} JPA audit fields that should be exposed:
 *
 * <pre>{@code
 * @Getter
 * @Setter
 * @Builder
 * public class ProductResponse extends AuditableDto {
 *     private UUID id;
 *     private String name;
 *     // createdAt and updatedAt inherited
 * }
 * }</pre>
 */
@Getter
public abstract class AuditableDto {

    /** Timestamp when the entity was first created. */
    private LocalDateTime createdAt;

    /** Timestamp when the entity was last modified. */
    private LocalDateTime updatedAt;

    /**
     * Sets the audit timestamps. Called by service mappers that populate
     * DTOs from JPA entities.
     *
     * @param createdAt when the entity was created; may be {@code null} for new entities
     * @param updatedAt when the entity was last updated; may be {@code null}
     */
    public void setAuditTimestamps(final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
