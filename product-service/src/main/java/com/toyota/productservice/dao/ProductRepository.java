package com.toyota.productservice.dao;

import com.toyota.productservice.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByState(boolean state, Pageable pageable);
    Page<Product> findByNameContaining(String name, Pageable pageable);
    List<Product> findByNameContaining(String name, Sort sort);
    boolean existsByName(String name);
}
