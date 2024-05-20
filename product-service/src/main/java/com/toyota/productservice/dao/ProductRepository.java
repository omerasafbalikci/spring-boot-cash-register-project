package com.toyota.productservice.dao;

import com.toyota.productservice.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing product table in database.
 */

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE LOWER(SUBSTRING(p.name, 1, 1)) = LOWER(?1)")
    Page<Product> findByInitialLetterIgnoreCase(String initialLetter, Pageable pageable);
    Product findByBarcodeNumber(String barcodeNumber);
    Boolean existsByNameIgnoreCase(String name);
    Product findByNameIgnoreCase(String name);
}
