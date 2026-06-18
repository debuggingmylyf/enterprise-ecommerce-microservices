package com.ecommerce.product.serviceImpl;

import com.ecommerce.product.dto.request.category.CreateCategoryRequest;
import com.ecommerce.product.dto.request.category.UpdateCategoryReqest;
import com.ecommerce.product.dto.response.category.CategoryResponse;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.exception.BusinessException;
import com.ecommerce.product.exception.ErrorCode;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.mapper.CategoryMapper;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link CategoryService}.
 *
 * <p>All write operations run inside a transaction. Read operations use the default
 * read-only transaction inherited from Spring's defaults.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * {@inheritDoc}
     *
     * @throws BusinessException         ({@link ErrorCode#DUPLICATE_CATEGORY_NAME}) if the name is taken
     */
    @Override
    @Transactional
    public CategoryResponse createCategory(final CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName().trim())) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_CATEGORY_NAME,
                    "A category with the name '" + request.getName() + "' already exists"
            );
        }

        Category category = categoryMapper.toEntity(request);

        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category not found with ID: " + request.getParentCategoryId()));
            category.setParentCategory(parent);
        }

        Category saved = categoryRepository.save(category);
        log.info("Category created: id={}, name={}", saved.getId(), saved.getName());
        return categoryMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the category ID does not exist
     * @throws BusinessException         ({@link ErrorCode#DUPLICATE_CATEGORY_NAME}) if another category
     *                                   already owns the requested name
     */
    @Override
    @Transactional
    public CategoryResponse updateCategory(final UUID id, final UpdateCategoryReqest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        if (categoryRepository.existsByNameAndIdNot(request.getName().trim(), id)) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_CATEGORY_NAME,
                    "A category with the name '" + request.getName() + "' already exists"
            );
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());

        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category not found with ID: " + request.getParentCategoryId()));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        Category updated = categoryRepository.save(category);
        log.info("Category updated: id={}, name={}", updated.getId(), updated.getName());
        return categoryMapper.toResponse(updated);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the category ID does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(final UUID id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByActiveTrue()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Performs a soft delete by setting {@code active = false}.
     *
     * @throws ResourceNotFoundException if the category ID does not exist
     */
    @Override
    @Transactional
    public void deleteCategory(final UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        category.setActive(false);
        categoryRepository.save(category);
        log.info("Category soft-deleted: id={}", id);
    }
}

