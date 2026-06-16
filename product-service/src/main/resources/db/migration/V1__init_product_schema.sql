-- V1__init_product_schema.sql

CREATE TABLE category (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    parent_category_id UUID,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_parent_category
        FOREIGN KEY(parent_category_id)
        REFERENCES category(id)
);

CREATE TABLE product (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(250) NOT NULL UNIQUE,
    short_description VARCHAR(500),
    full_description TEXT,
    brand VARCHAR(100),
    sku_code VARCHAR(100) NOT NULL UNIQUE,
    category_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_product_category
        FOREIGN KEY(category_id)
        REFERENCES category(id)
);

CREATE TABLE product_pricing (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL UNIQUE,
    base_price DECIMAL(12,2) NOT NULL,
    discount_price DECIMAL(12,2),
    currency VARCHAR(10) DEFAULT 'INR',
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_product_pricing
        FOREIGN KEY(product_id)
        REFERENCES product(id)
);

CREATE TABLE product_image (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    image_url TEXT NOT NULL,
    display_order INTEGER DEFAULT 1,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_product_image
        FOREIGN KEY(product_id)
        REFERENCES product(id)
);

CREATE TABLE product_attribute (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    attribute_name VARCHAR(100) NOT NULL,
    attribute_value VARCHAR(300) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_product_attribute
        FOREIGN KEY(product_id)
        REFERENCES product(id)
);

CREATE INDEX idx_product_name ON product(name);
CREATE INDEX idx_product_sku ON product(sku_code);
CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_attribute_name ON product_attribute(attribute_name);