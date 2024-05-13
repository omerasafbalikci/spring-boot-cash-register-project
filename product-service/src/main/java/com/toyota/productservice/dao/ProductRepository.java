package com.toyota.productservice.dao;

import com.toyota.productservice.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE (:id IS NULL OR p.id = :id) AND (:barcodeNumber IS NULL OR p.barcodeNumber LIKE CONCAT('%', :barcodeNumber, '%')) AND (:state IS NULL OR p.state = :state)")
    Page<Product> findByFilters(@Param("id") Long id, @Param("barcodeNumber") String barcodeNumber, @Param("state") Boolean state, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE LOWER(SUBSTRING(p.name, 1, 1)) = LOWER(?1)")
    Page<Product> findByInitialLetterIgnoreCase(String initialLetter, Pageable pageable);
    Product findByBarcodeNumber(String barcodeNumber);
    Boolean existsByNameIgnoreCase(String name);
    Product findByNameIgnoreCase(String name);
}
