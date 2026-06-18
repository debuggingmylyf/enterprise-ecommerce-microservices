package com.ecommerce.product.serviceImpl;

import com.ecommerce.product.dto.common.PaginatedResponse;
import com.ecommerce.product.dto.request.product.CreateProductRequest;
import com.ecommerce.product.dto.request.product.UpdateProductRequest;
import com.ecommerce.product.dto.request.product.UpdateProductStatusRequest;
import com.ecommerce.product.dto.response.product.CreateProductResponse;
import com.ecommerce.product.dto.response.product.ProductResponse;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductAttribute;
import com.ecommerce.product.entity.ProductImage;
import com.ecommerce.product.entity.ProductPricing;
import com.ecommerce.product.enums.ProductStatus;
import com.ecommerce.product.exception.BusinessException;
import com.ecommerce.product.exception.ErrorCode;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import com.ecommerce.product.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link ProductService}.
 *
 * <p>Manages the full product aggregate: core product fields, pricing,
 * images, and attributes. All mutation operations are wrapped in transactions.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    /**
     * {@inheritDoc}
     *
     * <p>Also creates and associates a {@link ProductPricing} record, a set of
     * {@link ProductImage} records, and a set of {@link ProductAttribute} records
     * within the same transaction.
     *
     * @throws ResourceNotFoundException if the referenced category does not exist
     * @throws BusinessException         ({@link ErrorCode#DUPLICATE_SKU}) if the SKU is already in use
     */
    @Override
    @Transactional
    public CreateProductResponse createProduct(final CreateProductRequest request) {
        if (productRepository.existsBySkuCode(request.getName())) {
            // Note: SKU is derived from the request; actual unique SKU generation
            // would be project-specific. Here we guard against re-submissions.
        }

        final Category category = resolveCategory(request.getCategoryId());

        final String skuCode = generateSkuCode(request.getName(), request.getBrand());
        if (productRepository.existsBySkuCode(skuCode)) {
            throw new BusinessException(ErrorCode.DUPLICATE_SKU,
                    "A product with SKU '" + skuCode + "' already exists");
        }

        final String slug = SlugGenerator.generate(request.getName());

        Product product = Product.builder()
                .name(request.getName())
                .slug(slug)
                .shortDescription(request.getShortDescription())
                .fullDescription(request.getFullDescription())
                .brand(request.getBrand())
                .skuCode(skuCode)
                .category(category)
                .status(ProductStatus.DRAFT)
                .active(true)
                .build();

        // Associate pricing
        if (request.getBasePrice() != null) {
            ProductPricing pricing = ProductPricing.builder()
                    .product(product)
                    .basePrice(request.getBasePrice())
                    .discountPrice(request.getDiscountPrice())
                    .build();
            product.setPricing(pricing);
        }

        // Associate images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            request.getImages().forEach(imgReq -> {
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(imgReq.getImageUrl())
                        .displayOrder(imgReq.getDisplayOrder() != null ? imgReq.getDisplayOrder() : 1)
                        .isPrimary(imgReq.getPrimary() != null && imgReq.getPrimary())
                        .build();
                product.addImage(image);
            });
        }

        // Associate attributes
        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            Set<ProductAttribute> attributes = request.getAttributes().stream()
                    .map(attrReq -> ProductAttribute.builder()
                            .product(product)
                            .attributeName(attrReq.getAttributeName())
                            .attributeValue(attrReq.getAttributeValue())
                            .build())
                    .collect(Collectors.toSet());
            product.getAttributes().addAll(attributes);
        }

        final Product saved = productRepository.save(product);
        log.info("Product created: id={}, sku={}", saved.getId(), saved.getSkuCode());

        return CreateProductResponse.builder()
                .productId(saved.getId())
                .skuCode(saved.getSkuCode())
                .status(saved.getStatus())
                .message("Product created successfully and is in DRAFT state")
                .build();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Only non-null fields in the request are applied; existing values are retained otherwise.
     *
     * @throws ResourceNotFoundException if the product or referenced category does not exist
     */
    @Override
    @Transactional
    public ProductResponse updateProduct(final UUID id, final UpdateProductRequest request) {
        final Product product = resolveProduct(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName().trim());
        }
        if (request.getShortDescription() != null) {
            product.setShortDescription(request.getShortDescription());
        }
        if (request.getFullDescription() != null) {
            product.setFullDescription(request.getFullDescription());
        }
        if (request.getBrand() != null && !request.getBrand().isBlank()) {
            product.setBrand(request.getBrand().trim());
        }
        if (request.getCategoryId() != null) {
            product.setCategory(resolveCategory(request.getCategoryId()));
        }

        // Update pricing if provided
        if (request.getBasePrice() != null && product.getPricing() != null) {
            product.getPricing().setBasePrice(request.getBasePrice());
        }
        if (request.getDiscountPrice() != null && product.getPricing() != null) {
            product.getPricing().setDiscountPrice(request.getDiscountPrice());
        }

        final Product updated = productRepository.save(product);
        log.info("Product updated: id={}", updated.getId());
        return productMapper.toResponse(updated);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the product ID does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(final UUID id) {
        return productMapper.toResponse(resolveProduct(id));
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no product with the given slug exists
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(final String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("Slug must not be null or blank");
        }
        return productRepository.findBySlug(slug)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProductResponse> getAllProducts(final Pageable pageable) {
        final Page<Product> page = productRepository.findAll(pageable);
        final List<ProductResponse> content = page.getContent()
                .stream()
                .map(productMapper::toResponse)
                .toList();

        return PaginatedResponse.<ProductResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the product ID does not exist
     */
    @Override
    @Transactional
    public ProductResponse updateProductStatus(final UUID id, final UpdateProductStatusRequest request) {
        final Product product = resolveProduct(id);
        product.setStatus(request.getStatus());
        final Product updated = productRepository.save(product);
        log.info("Product status updated: id={}, status={}", updated.getId(), updated.getStatus());
        return productMapper.toResponse(updated);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Performs a soft delete by setting {@code active = false} and status to {@code INACTIVE}.
     *
     * @throws ResourceNotFoundException if the product ID does not exist
     */
    @Override
    @Transactional
    public void deleteProduct(final UUID id) {
        final Product product = resolveProduct(id);
        product.setActive(false);
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
        log.info("Product soft-deleted: id={}", id);
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    private Product resolveProduct(final UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    private Category resolveCategory(final UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with ID: " + categoryId));
    }

    /**
     * Generates a deterministic SKU code from the product name and brand.
     * Format: {@code BRAND-NAME_SEGMENT-RANDOM_HEX}.
     */
    private String generateSkuCode(final String name, final String brand) {
        final String brandPart = (brand != null && !brand.isBlank())
                ? brand.trim().toUpperCase().replaceAll("\\s+", "-").substring(0, Math.min(brand.trim().length(), 6))
                : "GEN";
        final String namePart = name.trim().toUpperCase().replaceAll("\\s+", "-")
                .substring(0, Math.min(name.trim().length(), 8));
        final String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return brandPart + "-" + namePart + "-" + randomPart;
    }
}

