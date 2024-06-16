package com.toyota.productservice.service.rules;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for handling business rules related to product categories.
 */

@Service
@AllArgsConstructor
public class ProductCategoryBusinessRules {
    private final ProductCategoryRepository productCategoryRepository;

    /**
     * Checks if a product category with the given name already exists.
     *
     * @param name the name of the product category to check
     * @throws EntityAlreadyExistsException if a product category with the given name already exists
     */
    public void checkIfProductCategoryNameExists(String name) {
        if (this.productCategoryRepository.existsByNameIgnoreCase(name)) {
            throw new EntityAlreadyExistsException("Product category already exists");
        }
    }

    /**
     * Updates the attributes of the given product category based on the provided
     * update request. If any attribute in the update request is null, the corresponding
     * value from the existing product category will be retained.
     *
     * @param updateProductCategoryRequest the request containing new attribute values for the product category
     * @param productCategory the product category to be updated
     */
    public void checkUpdate(UpdateProductCategoryRequest updateProductCategoryRequest, ProductCategory productCategory) {
        if (updateProductCategoryRequest.getName() != null) {
            productCategory.setName(updateProductCategoryRequest.getName());
        }
        if (updateProductCategoryRequest.getDescription() != null) {
            productCategory.setDescription(updateProductCategoryRequest.getDescription());
        }
        if (updateProductCategoryRequest.getImageUrl() != null) {
            productCategory.setImageUrl(updateProductCategoryRequest.getImageUrl());
        }
        productCategory.setCreatedBy(updateProductCategoryRequest.getCreatedBy());
        productCategory.setUpdatedAt(LocalDateTime.now());
    }
}
