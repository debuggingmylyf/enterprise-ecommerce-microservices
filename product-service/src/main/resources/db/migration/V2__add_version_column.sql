-- V2__add_version_column.sql
-- Adds the optimistic locking 'version' column required by BaseEntity (@Version)
-- to all tables whose JPA entities extend BaseEntity.

ALTER TABLE category
    ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0 NOT NULL;

ALTER TABLE product
    ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0 NOT NULL;
