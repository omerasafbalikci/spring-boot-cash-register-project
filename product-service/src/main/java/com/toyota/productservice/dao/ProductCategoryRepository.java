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
    List<ProductCategory> findByNameContainingIgnoreCase(String name);
    ProductCategory findByCategoryNumber(String categoryNumber);
}
