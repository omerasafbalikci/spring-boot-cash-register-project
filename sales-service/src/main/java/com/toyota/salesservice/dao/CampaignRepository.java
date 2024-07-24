package com.toyota.salesservice.dao;

import com.toyota.salesservice.domain.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing campaign table in database.
 */

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {
    Boolean existsByNameIgnoreCaseAndDeletedIsFalse(String name);
    Optional<Campaign> findByIdAndDeletedFalse(Long id);
}
