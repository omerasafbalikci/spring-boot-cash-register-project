package com.toyota.productservice.service.rules;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductCategoryBusinessRules {
    @Autowired
    private final ProductCategoryRepository productCategoryRepository;

    public void checkIfProductCategoryNameExists(String name) {
        if (this.productCategoryRepository.existsByNameIgnoreCase(name)) {
            throw new EntityAlreadyExistsException("Product Category already exists");
        }
    }

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
