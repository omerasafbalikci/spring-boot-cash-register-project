package com.toyota.productservice.service.rules;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.dao.ProductRepository;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.service.abstracts.ProductService;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import com.toyota.productservice.utilities.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for handling business rules related to products.
 */

@Service
@AllArgsConstructor
public class ProductBusinessRules {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final Logger logger = LogManager.getLogger(ProductService.class);

    /**
     * Updates the given product with values from the provided update request if they are not null.
     * Checks if the product category exists and updates the product's category. Also checks for
     * duplicate product names to ensure uniqueness.
     *
     * @param updateProductRequest the request containing the update details
     * @param product              the product to be updated
     * @throws EntityNotFoundException      if the specified product category does not exist
     * @throws EntityAlreadyExistsException if a product with the same name already exists
     */
    public void checkUpdate(UpdateProductRequest updateProductRequest, Product product) {
        if (this.productRepository.existsByNameIgnoreCaseAndDeletedIsFalse(updateProductRequest.getName()) && !product.getName().equals(updateProductRequest.getName())) {
            logger.warn("Product name already exists: {}", product.getName());
            throw new EntityAlreadyExistsException("Product name already exists");
        }

        if (updateProductRequest.getName() != null) {
            product.setName(updateProductRequest.getName());
        }
        if (updateProductRequest.getDescription() != null) {
            product.setDescription(updateProductRequest.getDescription());
        }
        if (updateProductRequest.getQuantity() != null) {
            product.setQuantity(updateProductRequest.getQuantity());
        }
        if (updateProductRequest.getUnitPrice() != null) {
            product.setUnitPrice(updateProductRequest.getUnitPrice());
        }
        if (updateProductRequest.getState() != null) {
            product.setState(updateProductRequest.getState());
        }
        if (updateProductRequest.getImageUrl() != null) {
            product.setImageUrl(updateProductRequest.getImageUrl());
        }
        if (updateProductRequest.getProductCategoryId() != null) {
            Optional<ProductCategory> optionalProductCategory = this.productCategoryRepository.findByIdAndDeletedFalse(updateProductRequest.getProductCategoryId());
            if (optionalProductCategory.isPresent()) {
                ProductCategory productCategory = optionalProductCategory.get();
                product.setProductCategory(productCategory);
            } else {
                logger.warn("Product category not found with id: {}", updateProductRequest.getProductCategoryId());
                throw new EntityNotFoundException("Product category not found.");
            }
        }
        product.setCreatedBy(updateProductRequest.getCreatedBy());
        product.setUpdatedAt(LocalDateTime.now());
    }
}
