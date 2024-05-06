package com.toyota.productservice.service.rules;

import com.toyota.productservice.dao.ProductRepository;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ProductBusinessRules {
    private final ProductRepository productRepository;

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
            throw new EntityAlreadyExistsException("Product name already exists");
        }
    }
}
