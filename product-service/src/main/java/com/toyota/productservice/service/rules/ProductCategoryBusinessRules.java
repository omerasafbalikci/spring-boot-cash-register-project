package com.toyota.productservice.service.rules;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryBusinessRules {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public void checkIfProductCategoryNameExists(String name) {
        if (this.productCategoryRepository.existsByName(name)) {
            throw new EntityAlreadyExistsException("Product Category already exists");
        }
    }
}
