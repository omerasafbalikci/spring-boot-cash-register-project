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
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing campaigns.
 */

@Service
@Transactional
@AllArgsConstructor
public class CampaignManager implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final Logger logger = LogManager.getLogger(CampaignService.class);
    private final ModelMapperService modelMapperService;
    private final CampaignBusinessRules campaignBusinessRules;

    /**
     * Retrieves all campaigns.
     *
     * @return a list of GetAllCampaignsResponse objects representing all campaigns.
     */
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

    /**
     * Retrieves a campaign by its campaign number.
     *
     * @param campaignNumber the campaign number.
     * @return a GetAllCampaignsResponse object representing the campaign.
     * @throws CampaignNotFoundException if no campaign is found with the given campaign number.
     */
    @Override
    public GetAllCampaignsResponse getCampaignByCampaignNumber(String campaignNumber) {
        logger.info("Fetching campaign by campaign number '{}'.", campaignNumber);
        Optional<Campaign> optionalCampaign = this.campaignRepository.findByCampaignNumber(campaignNumber);
        if (optionalCampaign.isPresent()) {
            Campaign campaign = optionalCampaign.get();
            logger.debug("Retrieved campaign with campaign number '{}'.", campaignNumber);
            return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
        } else {
            logger.warn("No campaign found with category number '{}'.", campaignNumber);
            throw new CampaignNotFoundException("Campaign not found for campaign number: " + campaignNumber);
        }
    }

    /**
     * Adds a new campaign.
     *
     * @param createCampaignRequest the request object containing the campaign details.
     * @return a GetAllCampaignsResponse object representing the added campaign.
     * @throws CampaignAlreadyExistsException if a campaign with the same name already exists.
     */
    @Override
    public GetAllCampaignsResponse addCampaign(CreateCampaignRequest createCampaignRequest) {
        logger.info("Adding new campaign: '{}'.", createCampaignRequest.getName());
        if (this.campaignRepository.existsByNameIgnoreCase(createCampaignRequest.getName())) {
            logger.warn("Campaign already exists with name '{}'.", createCampaignRequest.getName());
            throw new CampaignAlreadyExistsException("Campaign already exists");
        }
        logger.debug("Mapping CreateCampaignRequest to Campaign entity.");
        Campaign campaign = this.modelMapperService.forRequest().map(createCampaignRequest, Campaign.class);
        logger.debug("Checking campaign details.");
        this.campaignBusinessRules.checkCampaignDetails(campaign);
        logger.debug("Adding campaign type.");
        this.campaignBusinessRules.addCampaignType(campaign, createCampaignRequest);
        campaign.setCampaignNumber(UUID.randomUUID().toString().substring(0, 8));
        campaign.setUpdatedAt(LocalDateTime.now());
        this.campaignRepository.save(campaign);
        logger.debug("New campaign added: '{}'.", createCampaignRequest.getName());
        return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
    }

    /**
     * Updates an existing campaign.
     *
     * @param updateCampaignRequest the request object containing the updated campaign details.
     * @return a GetAllCampaignsResponse object representing the updated campaign.
     * @throws CampaignNotFoundException if no campaign is found with the given ID.
     */
    @Override
    public GetAllCampaignsResponse updateCampaign(UpdateCampaignRequest updateCampaignRequest) {
        logger.info("Updating campaign with id '{}'.", updateCampaignRequest.getId());
        Campaign existingCampaign = this.campaignRepository.findById(updateCampaignRequest.getId()).orElseThrow(() -> {
            logger.warn("No campaign found with id '{}'.", updateCampaignRequest.getId());
            return new CampaignNotFoundException("Campaign not found");
        });
        logger.debug("Mapping CreateCampaignRequest to Campaign entity.");
        Campaign campaign = this.modelMapperService.forRequest().map(updateCampaignRequest, Campaign.class);
        logger.debug("Checking update rules for the campaign.");
        this.campaignBusinessRules.checkUpdate(campaign, existingCampaign);
        logger.info("Campaign name does not exist. Proceeding with creating the campaign.");
        logger.debug("Checking campaign details.");
        this.campaignBusinessRules.checkCampaignDetails(campaign);
        logger.debug("Updating campaign type.");
        this.campaignBusinessRules.updateCampaignType(campaign, updateCampaignRequest);

        campaign.setCampaignNumber(existingCampaign.getCampaignNumber());
        campaign.setUpdatedAt(LocalDateTime.now());
        this.campaignRepository.save(campaign);
        logger.debug("Campaign with id '{}' updated successfully.", updateCampaignRequest.getId());
        return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
    }

    /**
     * Deletes a campaign by its ID.
     *
     * @param id the ID of the campaign to be deleted.
     * @return a GetAllCampaignsResponse object representing the deleted campaign.
     * @throws CampaignNotFoundException if no campaign is found with the given ID.
     */
    @Override
    public GetAllCampaignsResponse deleteCampaign(Long id) {
        logger.info("Deleting campaign with id '{}'.", id);
        Optional<Campaign> optionalCampaign = this.campaignRepository.findById(id);

        if (optionalCampaign.isPresent()) {
            Campaign campaign = optionalCampaign.get();
            this.campaignRepository.deleteById(id);
            logger.debug("Campaign with id '{}' deleted successfully.", id);
            return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
        } else {
            logger.warn("No campaign found with id '{}'.", id);
            throw new CampaignNotFoundException("Campaign not found");
        }
    }

    /**
     * Deletes all campaigns.
     *
     * @throws CampaignNotFoundException if no campaigns are found to delete.
     */
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
