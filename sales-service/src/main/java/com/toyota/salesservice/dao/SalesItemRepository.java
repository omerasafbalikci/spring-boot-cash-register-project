package com.toyota.salesservice.dao;

import com.toyota.salesservice.domain.SalesItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing sales items table in database.
 */

@Repository
public interface SalesItemRepository extends JpaRepository<SalesItems, Long> {
}
