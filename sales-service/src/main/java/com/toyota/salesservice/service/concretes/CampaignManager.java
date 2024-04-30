package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import com.toyota.salesservice.dto.responses.GetAllCampaignsResponse;
import com.toyota.salesservice.service.abstracts.CampaignService;
import com.toyota.salesservice.service.rules.CampaignBusinessRules;
import com.toyota.salesservice.utilities.exceptions.CampaignAlreadyExistsException;
import com.toyota.salesservice.utilities.exceptions.CampaignNotFoundException;
import com.toyota.salesservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.TreeMap;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class CampaignManager implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final Logger logger = LogManager.getLogger(CampaignService.class);
    private final ModelMapperService modelMapperService;
    private final CampaignBusinessRules campaignBusinessRules;

    @Override
    public TreeMap<String, Object> getAllCampaignsPage(int page, int size, String[] sort) {
        return null;
    }

    @Override
    public GetAllCampaignsResponse addCampaign(CreateCampaignRequest createCampaignRequest) {
        if (this.campaignRepository.existsByNameIgnoreCase(createCampaignRequest.getName())) {
            throw new CampaignAlreadyExistsException("Campaign already exists");
        }
        Campaign campaign = this.modelMapperService.forRequest().map(createCampaignRequest, Campaign.class);
        campaign.setCampaignNumber(UUID.randomUUID().toString().substring(0, 8));
        this.campaignRepository.save(campaign);
        return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
    }

    @Override
    public GetAllCampaignsResponse updateCampaign(Long id, CreateCampaignRequest createCampaignRequest) {
        Campaign existingCampaign = this.campaignRepository.findById(id).orElseThrow(() -> {
            return new CampaignNotFoundException("Campaign not found");
        });
        Campaign campaign = this.modelMapperService.forRequest().map(createCampaignRequest, Campaign.class);
        this.campaignBusinessRules.checkUpdate(campaign, existingCampaign);
        if (this.campaignRepository.existsByNameIgnoreCase(campaign.getName()) && !existingCampaign.getName().equals(campaign.getName())) {
            throw new CampaignAlreadyExistsException("Campaign already exists");
        }
        campaign.setCampaignNumber(existingCampaign.getCampaignNumber());
        this.campaignRepository.save(campaign);
        return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
    }

    @Override
    public GetAllCampaignsResponse deleteCampaign(Long id) {
        Campaign campaign = this.campaignRepository.findById(id).orElseThrow(() -> {
            return new CampaignNotFoundException("Product not found");
        });
        this.campaignRepository.deleteById(id);
        return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
    }
}
