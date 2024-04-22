package com.toyota.productservice.dao;

import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Boolean existsByName(String name);
    @Query("SELECT pc FROM ProductCategory pc WHERE LOWER(pc.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    ProductCategory findByNameIgnoreCase(String name);
    ProductCategory findByCategoryNumber(String categoryNumber);
}
