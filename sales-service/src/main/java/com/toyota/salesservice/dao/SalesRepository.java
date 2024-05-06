package com.toyota.salesservice.dao;

import com.toyota.salesservice.domain.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
    @Query("SELECT s FROM Sales s WHERE " +
            "(?1 IS NULL OR s.id = ?1) AND " +
            "(UPPER(s.salesNumber) LIKE CONCAT('%', UPPER(?2), '%')) AND " +
            "(?3 IS NULL OR s.salesDate = ?3) AND " +
            "(UPPER(s.createdBy) LIKE CONCAT('%', UPPER(?4), '%')) AND " +
            "(UPPER(s.paymentType) LIKE CONCAT('%', UPPER(?5), '%')) AND " +
            "(?6 IS NULL OR s.totalPrice = ?6) AND " +
            "(?7 IS NULL OR s.money = ?7) AND " +
            "(?8 IS NULL OR s.change = ?8)")
    Page<Sales> getSalesFiltered(Long id, String salesNumber, LocalDateTime salesDate, String createdBy, String paymentType,
                                 Double totalPrice, Double money, Double change, Pageable pageable);
    Sales findBySalesNumber(String salesNumber);
}
