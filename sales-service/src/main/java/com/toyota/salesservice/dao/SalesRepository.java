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
            "(UPPER(s.id) = COALESCE(NULLIF(?1, ''), UPPER(s.id))) AND " +
            "(UPPER(s.salesNumber) LIKE CONCAT('%', UPPER(?2), '%')) AND " +
            "(s.salesDate = COALESCE(NULLIF(?3, ''), s.salesDate)) AND " +
            "(UPPER(s.createdBy) LIKE CONCAT('%', UPPER(?4), '%')) AND " +
            "(UPPER(s.paymentType) LIKE CONCAT('%', UPPER(?5), '%')) AND " +
            "(s.totalPrice = COALESCE(NULLIF(?6, 0.0), s.totalPrice)) AND " +
            "(s.money = COALESCE(NULLIF(?7, 0.0), s.money)) AND " +
            "(s.change = COALESCE(NULLIF(?8, 0.0), s.change))")
    Page<Sales> getSalesFiltered(Long id, String salesNumber, LocalDateTime salesDate, String createdBy, String paymentType,
                                 Double totalPrice, Double money, Double change, Pageable pageable);
    Sales findBySalesNumber(String salesNumber);
}
