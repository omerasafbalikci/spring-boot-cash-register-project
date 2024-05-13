package com.toyota.productservice.dao;

import com.toyota.productservice.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE " +
            "(?1 IS NULL OR p.id = ?1) AND " +
            "(UPPER(p.barcodeNumber) LIKE CONCAT('%', UPPER(?2), '%')) AND " +
            "(?3 IS NULL OR p.state = ?3)")
    Page<Product> getProductFiltered(Long id, String barcodeNumber, Boolean state, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE LOWER(SUBSTRING(p.name, 1, 1)) = LOWER(?1)")
    Page<Product> findByInitialLetterIgnoreCase(String initialLetter, Pageable pageable);
    Product findByBarcodeNumber(String barcodeNumber);
    Boolean existsByNameIgnoreCase(String name);
    Product findByNameIgnoreCase(String name);
}
