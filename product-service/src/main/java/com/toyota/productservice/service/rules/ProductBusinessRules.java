package com.toyota.productservice.service.rules;

import com.toyota.productservice.dao.ProductRepository;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductBusinessRules {
    private final ProductRepository productRepository;

    public void checkIfSkuCodeExists(String skuCode) {
        if (this.productRepository.existsBySkuCodeIgnoreCase(skuCode)) {
            throw new EntityAlreadyExistsException("Product skuCode already exists");
        }
    }

    public void checkUpdate(Product product, Product existingProduct) {
        if (product.getSkuCode() == null) {
            product.setSkuCode(existingProduct.getSkuCode());
        }
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
    }
}
