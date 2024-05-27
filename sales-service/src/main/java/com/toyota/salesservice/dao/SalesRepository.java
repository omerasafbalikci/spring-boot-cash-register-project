package com.toyota.salesservice.dao;

import com.toyota.salesservice.domain.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing sales table in database.
 */

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long>, JpaSpecificationExecutor<Sales> {
    Sales findBySalesNumber(String salesNumber);
}
