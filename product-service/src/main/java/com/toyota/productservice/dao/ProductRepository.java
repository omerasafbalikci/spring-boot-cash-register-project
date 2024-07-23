package com.toyota.productservice.dao;

import com.toyota.productservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing product table in database.
 */

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByIdAndDeletedFalse(Long id);
    Optional<Product> findByBarcodeNumberAndDeletedFalse(String barcodeNumber);
    Boolean existsByNameIgnoreCaseAndDeletedIsFalse(String name);
    Optional<Product> findByNameIgnoreCaseAndDeletedFalse(String name);
}