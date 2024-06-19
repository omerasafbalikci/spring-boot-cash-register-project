package com.toyota.productservice.dao;

import com.toyota.productservice.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing product_category table in database.
 */

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Boolean existsByNameIgnoreCase(String name);
    List<ProductCategory> findByNameContainingIgnoreCase(String name);
}
