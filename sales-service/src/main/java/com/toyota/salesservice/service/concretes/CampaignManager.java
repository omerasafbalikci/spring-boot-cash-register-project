package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import com.toyota.salesservice.dto.requests.UpdateCampaignRequest;
import com.toyota.salesservice.dto.responses.GetAllCampaignsResponse;
import com.toyota.salesservice.service.abstracts.CampaignService;
import com.toyota.salesservice.service.rules.CampaignBusinessRules;
import com.toyota.salesservice.utilities.exceptions.CampaignAlreadyExistsException;
import com.toyota.salesservice.utilities.exceptions.CampaignNotFoundException;
import com.toyota.salesservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public List<GetAllCampaignsResponse> getAllCampaigns() {
        logger.info("Fetching all campaigns.");
        List<Campaign> campaigns = this.campaignRepository.findAll();
        logger.debug("Retrieved {} campaigns.", campaigns.size());
        List<GetAllCampaignsResponse> responses = campaigns.stream()
                .map(campaign -> this.modelMapperService.forResponse()
                        .map(campaign, GetAllCampaignsResponse.class)).toList();
        logger.info("Retrieved and converted {} campaigns to GetAllCampaignsResponse.", responses.size());
        return responses;
    }

    @Override
    public GetAllCampaignsResponse getCampaignByCampaignNumber(String campaignNumber) {
        logger.info("Fetching campaign by campaign number '{}'.", campaignNumber);
        Campaign campaign = this.campaignRepository.findByCampaignNumber(campaignNumber);
        if (campaign != null) {
            logger.debug("Retrieved campaign with campaign number '{}'.", campaignNumber);
            return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
        } else {
            logger.warn("No campaign found with category number '{}'.", campaignNumber);
            throw new CampaignNotFoundException("Campaign not found for campaign number: " + campaignNumber);
        }
    }

    @Override
    public GetAllCampaignsResponse addCampaign(CreateCampaignRequest createCampaignRequest) {
        logger.info("Adding new campaign: '{}'.", createCampaignRequest.getName());
        if (this.campaignRepository.existsByNameIgnoreCase(createCampaignRequest.getName())) {
            logger.warn("Campaign already exists with name '{}'.", createCampaignRequest.getName());
            throw new CampaignAlreadyExistsException("Campaign already exists");
        }
        Campaign campaign = this.modelMapperService.forRequest().map(createCampaignRequest, Campaign.class);
        this.campaignBusinessRules.checkCampaignDetails(campaign);
        this.campaignBusinessRules.addCampaignType(campaign, createCampaignRequest);
        campaign.setCampaignNumber(UUID.randomUUID().toString().substring(0, 8));
        campaign.setUpdatedAt(LocalDateTime.now());
        this.campaignRepository.save(campaign);
        logger.debug("New campaign added: '{}'.", createCampaignRequest.getName());
        return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
    }

    @Override
    public GetAllCampaignsResponse updateCampaign(UpdateCampaignRequest updateCampaignRequest) {
        logger.info("Updating campaign with id '{}'.", updateCampaignRequest.getId());
        Campaign existingCampaign = this.campaignRepository.findById(updateCampaignRequest.getId()).orElseThrow(() -> {
            logger.warn("No campaign found with id '{}'.", updateCampaignRequest.getId());
            return new CampaignNotFoundException("Campaign not found");
        });
        Campaign campaign = this.modelMapperService.forRequest().map(updateCampaignRequest, Campaign.class);
        this.campaignBusinessRules.checkUpdate(campaign, existingCampaign);
        this.campaignBusinessRules.checkCampaignDetails(campaign);
        this.campaignBusinessRules.updateCampaignType(campaign, updateCampaignRequest);
        logger.info("Campaign name does not exist. Proceeding with creating the campaign.");

        campaign.setCampaignNumber(existingCampaign.getCampaignNumber());
        campaign.setUpdatedAt(LocalDateTime.now());
        this.campaignRepository.save(campaign);
        logger.debug("Campaign with id '{}' updated successfully.", updateCampaignRequest.getId());
        return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
    }

    @Override
    public GetAllCampaignsResponse deleteCampaign(Long id) {
        logger.info("Deleting campaign with id '{}'.", id);
        Campaign campaign = this.campaignRepository.findById(id).orElseThrow(() -> {
            logger.warn("No campaign found with id '{}'.", id);
            return new CampaignNotFoundException("Campaign not found");
        });
        this.campaignRepository.deleteById(id);
        logger.debug("Campaign with id '{}' deleted successfully.", id);
        return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
    }

    @Override
    public void deleteAllCampaigns() {
        logger.info("Deleting all campaigns.");
        List<Campaign> campaigns = this.campaignRepository.findAll();
        if (campaigns.isEmpty()) {
            logger.warn("No campaigns found to delete.");
            throw new CampaignNotFoundException("Campaign not found");
        }
        this.campaignRepository.deleteAll();
        logger.debug("All campaigns deleted successfully.");
    }
}
