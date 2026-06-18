package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.response.category.CategoryResponse;
import com.ecommerce.product.dto.response.product.ProductAttributeResponse;
import com.ecommerce.product.dto.response.product.ProductImageResponse;
import com.ecommerce.product.dto.response.product.ProductPricingResponse;
import com.ecommerce.product.dto.response.product.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductAttribute;
import com.ecommerce.product.entity.ProductImage;
import com.ecommerce.product.entity.ProductPricing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring-managed mapper responsible for converting {@link Product} aggregates
 * (and their child entities) into their corresponding response DTOs.
 *
 * <p>Manual mapping is used intentionally (MapStruct is not on the classpath).
 * Delegates category mapping to {@link CategoryMapper}.
 */
@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    /**
     * Maps a {@link Product} entity and its full aggregate to a {@link ProductResponse} DTO.
     *
     * @param product the source entity; must not be {@code null}
     * @return a fully populated {@link ProductResponse}; never {@code null}
     * @throws IllegalArgumentException if {@code product} is {@code null}
     */
    public ProductResponse toResponse(final Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }

        final CategoryResponse categoryResponse = product.getCategory() != null
                ? categoryMapper.toResponse(product.getCategory())
                : null;

        final ProductPricingResponse pricingResponse = product.getPricing() != null
                ? toPricingResponse(product.getPricing())
                : null;

        final List<ProductImageResponse> imageResponses = product.getImages() != null
                ? product.getImages().stream().map(this::toImageResponse).toList()
                : Collections.emptyList();

        final Set<ProductAttributeResponse> attributeResponses = product.getAttributes() != null
                ? product.getAttributes().stream()
                        .map(this::toAttributeResponse)
                        .collect(Collectors.toSet())
                : Collections.emptySet();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .shortDescription(product.getShortDescription())
                .fullDescription(product.getFullDescription())
                .brand(product.getBrand())
                .skuCode(product.getSkuCode())
                .status(product.getStatus())
                .category(categoryResponse)
                .pricing(pricingResponse)
                .images(imageResponses)
                .attributes(attributeResponses)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    /**
     * Maps a {@link ProductPricing} entity to its {@link ProductPricingResponse} DTO.
     *
     * @param pricing the source entity; must not be {@code null}
     * @return a populated {@link ProductPricingResponse}; never {@code null}
     * @throws IllegalArgumentException if {@code pricing} is {@code null}
     */
    public ProductPricingResponse toPricingResponse(final ProductPricing pricing) {
        if (pricing == null) {
            throw new IllegalArgumentException("ProductPricing must not be null");
        }

        return ProductPricingResponse.builder()
                .id(pricing.getId())
                .basePrice(pricing.getBasePrice())
                .discountPrice(pricing.getDiscountPrice())
                .currency(pricing.getCurrency())
                .effectiveFrom(pricing.getEffectiveFrom())
                .effectiveTo(pricing.getEffectiveTo())
                .build();
    }

    /**
     * Maps a {@link ProductImage} entity to its {@link ProductImageResponse} DTO.
     *
     * @param image the source entity; must not be {@code null}
     * @return a populated {@link ProductImageResponse}; never {@code null}
     * @throws IllegalArgumentException if {@code image} is {@code null}
     */
    public ProductImageResponse toImageResponse(final ProductImage image) {
        if (image == null) {
            throw new IllegalArgumentException("ProductImage must not be null");
        }

        return ProductImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .displayOrder(image.getDisplayOrder())
                .isPrimary(image.getIsPrimary())
                .build();
    }

    /**
     * Maps a {@link ProductAttribute} entity to its {@link ProductAttributeResponse} DTO.
     *
     * @param attribute the source entity; must not be {@code null}
     * @return a populated {@link ProductAttributeResponse}; never {@code null}
     * @throws IllegalArgumentException if {@code attribute} is {@code null}
     */
    public ProductAttributeResponse toAttributeResponse(final ProductAttribute attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("ProductAttribute must not be null");
        }

        return ProductAttributeResponse.builder()
                .id(attribute.getId())
                .attributeName(attribute.getAttributeName())
                .attributeValue(attribute.getAttributeValue())
                .build();
    }
}

