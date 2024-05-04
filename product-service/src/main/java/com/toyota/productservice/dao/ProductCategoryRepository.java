package com.toyota.productservice.dao;

import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Boolean existsByNameIgnoreCase(String name);
    @Query("SELECT pc FROM ProductCategory pc WHERE LOWER(pc.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<ProductCategory> findByNameContainingIgnoreCase(String name);
    ProductCategory findByCategoryNumber(String categoryNumber);
}
