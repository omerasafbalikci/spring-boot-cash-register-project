package com.toyota.productservice.service.rules;

import com.toyota.productservice.dao.ProductRepository;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ProductBusinessRules {
    @Autowired
    private ProductRepository productRepository;

    public void checkIfProductNameExists(String name) {
        if (this.productRepository.existsByName(name)) {
            throw new EntityAlreadyExistsException("Product already exists");
        }
    }
}
