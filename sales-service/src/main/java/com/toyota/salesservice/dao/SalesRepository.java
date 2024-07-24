package com.toyota.salesservice.dao;

import com.toyota.salesservice.domain.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing sales table in database.
 */

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long>, JpaSpecificationExecutor<Sales> {
    Optional<Sales> findBySalesNumberAndDeletedFalse(String salesNumber);
    List<Sales> findBySalesDateBetweenAndDeletedFalse(LocalDateTime startDate, LocalDateTime endDate);
}
