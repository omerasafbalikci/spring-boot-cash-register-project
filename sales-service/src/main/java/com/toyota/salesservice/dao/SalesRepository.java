package com.toyota.salesservice.dao;

import com.toyota.salesservice.domain.Sales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepository extends JpaRepository<Sales, Long> {
    Sales findBySalesNumber(String salesNumber);
}
