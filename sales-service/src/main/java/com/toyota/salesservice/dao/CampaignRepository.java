package com.toyota.salesservice.dao;

import com.toyota.salesservice.domain.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Boolean existsByNameIgnoreCase(String name);
    Campaign findByCampaignNumber(String campaignNumber);
}
