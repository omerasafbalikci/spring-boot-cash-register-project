package com.toyota.productservice.dao;

import com.toyota.productservice.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing product_category table in database.
 */

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long>, JpaSpecificationExecutor<ProductCategory> {
    Boolean existsByNameIgnoreCaseAndDeletedIsFalse(String name);
    Optional<ProductCategory> findByIdAndDeletedFalse(Long id);
}
