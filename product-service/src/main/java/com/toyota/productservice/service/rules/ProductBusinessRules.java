package com.toyota.productservice.service.rules;

import com.toyota.productservice.dao.ProductRepository;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.service.abstracts.ProductService;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Service for handling business rules related to products.
 */

@Service
@AllArgsConstructor
public class ProductBusinessRules {
    private final ProductRepository productRepository;
    private final Logger logger = LogManager.getLogger(ProductService.class);

    /**
     * Checks and updates the given product's attributes. If any attribute is null,
     * it will be set to the corresponding value from the existing product.
     *
     * @param product          the product to update
     * @param existingProduct  the existing product with current attributes
     * @throws EntityAlreadyExistsException if the product name already exists
     */
    public void checkUpdate(Product product, Product existingProduct) {
        if (product.getName() == null) {
            product.setName(existingProduct.getName());
        }
        if (product.getDescription() == null) {
            product.setDescription(existingProduct.getDescription());
        }
        if (product.getQuantity() == null) {
            product.setQuantity(existingProduct.getQuantity());
        }
        if (product.getUnitPrice() == null) {
            product.setUnitPrice(existingProduct.getUnitPrice());
        }
        if (product.getState() == null) {
            product.setState(existingProduct.getState());
        }
        if (product.getImageUrl() == null) {
            product.setImageUrl(existingProduct.getImageUrl());
        }
        if (product.getProductCategory() == null) {
            product.setProductCategory(existingProduct.getProductCategory());
        }

        if (this.productRepository.existsByNameIgnoreCase(product.getName()) && !existingProduct.getName().equals(product.getName())) {
            logger.warn("Product name already exists: " + product.getName());
            throw new EntityAlreadyExistsException("Product name already exists");
        }
    }
}
