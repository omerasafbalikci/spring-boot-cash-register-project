package com.toyota.productservice.dao;

import com.toyota.productservice.domain.Product;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByState(Boolean state, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE LOWER(SUBSTRING(p.name, 1, 1)) = LOWER(?1)")
    Page<Product> findByInitialLetterIgnoreCase(String initialLetter, Pageable pageable);
    Product findByBarcodeNumber(String barcodeNumber);
    Product findByNameIgnoreCase(String name);
}
