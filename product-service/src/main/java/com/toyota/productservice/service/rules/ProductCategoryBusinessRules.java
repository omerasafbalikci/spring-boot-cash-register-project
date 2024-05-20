package com.toyota.productservice.service.rules;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
            throw new EntityAlreadyExistsException("Product Category already exists");
        }
    }

    /**
     * Updates the given product category's attributes. If any attribute is null,
     * it will be set to the corresponding value from the existing product category.
     *
     * @param productCategory the product category to update
     * @param existingProductCategory the existing product category with current attributes
     */
    public void checkUpdate(ProductCategory productCategory, ProductCategory existingProductCategory) {
        if (productCategory.getName() == null) {
            productCategory.setName(existingProductCategory.getName());
        }
        if (productCategory.getDescription() == null) {
            productCategory.setDescription(existingProductCategory.getDescription());
        }
        if (productCategory.getImageUrl() == null) {
            productCategory.setImageUrl(existingProductCategory.getImageUrl());
        }
    }
}
