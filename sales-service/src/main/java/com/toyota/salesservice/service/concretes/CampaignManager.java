package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.dao.CampaignSpecification;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
     * Fetches a paginated and filtered list of campaigns.
     *
     * @param page the page number to retrieve
     * @param size the size of the page to retrieve
     * @param sort the sorting criteria
     * @param id the ID filter
     * @param campaignNumber the campaign number filter
     * @param name the name filter
     * @param campaignCategory the campaign category filter
     * @param campaignKey the campaign key filter
     * @param state the state filter
     * @param createdBy the creator filter
     * @return a map containing the filtered campaigns and pagination details
     */
    @Override
    public Map<String, Object> getCampaignsFiltered(int page, int size, String[] sort, Long id, String campaignNumber, String name, String campaignCategory,
                                                    String campaignKey, Boolean state, String createdBy) {
        logger.info("Fetching all campaigns with pagination. Page: {}, Size: {}, Sort: {}. Filter: id={}, campaignNumber={}, name={}, campaignCategory={}, campaignKey={}, state={}, createdBy={}.", page, size, Arrays.toString(sort), id, campaignNumber, name, campaignCategory, campaignKey, state, createdBy);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        CampaignSpecification specification = new CampaignSpecification(id, campaignNumber, name, campaignCategory, campaignKey, state, createdBy);
        Page<Campaign> campaignPage = this.campaignRepository.findAll(specification, pagingSort);

        List<GetAllCampaignsResponse> responses = campaignPage.getContent().stream()
                .map(campaign -> this.modelMapperService.forResponse()
                        .map(campaign, GetAllCampaignsResponse.class)).collect(Collectors.toList());
        logger.debug("Get campaigns: Mapped campaigns to response DTOs. Number of campaigns: {}", responses.size());

        Map<String, Object> response = new HashMap<>();
        response.put("campaigns", responses);
        response.put("currentPage", campaignPage.getNumber());
        response.put("totalItems", campaignPage.getTotalElements());
        response.put("totalPages", campaignPage.getTotalPages());
        logger.debug("Get campaigns: Retrieved {} campaigns for page {}. Total items: {}. Total pages: {}.", responses.size(), campaignPage.getNumber(), campaignPage.getTotalElements(), campaignPage.getTotalPages());
        return response;
    }

    /**
     * Determines the sorting direction.
     *
     * @param direction the direction to sort (asc or desc)
     * @return the Sort.Direction enum value
     */
    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    /**
     * Creates a list of Sort.Order objects based on the provided sort parameters.
     *
     * @param sort the sort parameters
     * @return a list of Sort.Order objects
     */
    private List<Sort.Order> getOrder(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return orders;
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
        if (this.campaignRepository.existsByNameIgnoreCaseAndDeletedIsFalse(createCampaignRequest.getName())) {
            logger.warn("Campaign already exists with name '{}'.", createCampaignRequest.getName());
            throw new CampaignAlreadyExistsException("Campaign already exists");
        }
        logger.debug("Mapping CreateCampaignRequest to Campaign entity.");
        Campaign campaign = new Campaign();
        campaign.setName(createCampaignRequest.getName());
        campaign.setCampaignCategory(createCampaignRequest.getCampaignCategory());
        campaign.setCampaignKey(createCampaignRequest.getCampaignKey());
        campaign.setState(createCampaignRequest.getState());
        campaign.setCreatedBy(createCampaignRequest.getCreatedBy());
        logger.debug("Add campaign: Checking campaign details.");
        this.campaignBusinessRules.checkCampaignDetails(campaign);
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
        Optional<Campaign> optionalCampaign = this.campaignRepository.findByIdAndDeletedFalse(updateCampaignRequest.getId());
        if (optionalCampaign.isPresent()) {
            Campaign existingCampaign = optionalCampaign.get();
            logger.debug("Mapping UpdateCampaignRequest to Campaign entity.");
            Campaign campaign = new Campaign();
            campaign.setId(updateCampaignRequest.getId());
            campaign.setName(updateCampaignRequest.getName());
            campaign.setCampaignCategory(updateCampaignRequest.getCampaignCategory());
            campaign.setCampaignKey(updateCampaignRequest.getCampaignKey());
            campaign.setState(updateCampaignRequest.getState());
            campaign.setCreatedBy(updateCampaignRequest.getCreatedBy());
            logger.debug("Update campaign: Checking campaign details.");
            this.campaignBusinessRules.checkCampaignDetails(campaign);
            logger.debug("Checking update rules for the campaign.");
            this.campaignBusinessRules.checkUpdate(campaign, existingCampaign);
            campaign.setCampaignNumber(existingCampaign.getCampaignNumber());
            campaign.setUpdatedAt(LocalDateTime.now());
            this.campaignRepository.save(campaign);
            logger.debug("Campaign with id '{}' updated successfully.", updateCampaignRequest.getId());
            return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
        } else {
            logger.warn("Campaign with ID '{}' not found for update.", updateCampaignRequest.getId());
            throw new CampaignNotFoundException("Campaign not found");
        }
    }

    /**
     * Marks a campaign as deleted by its ID.
     *
     * @param id the ID of the campaign to be marked as deleted.
     * @return a {@link GetAllCampaignsResponse} object representing the campaign that was marked as deleted.
     * @throws CampaignNotFoundException if no campaign is found with the given ID.
     */
    @Override
    public GetAllCampaignsResponse deleteCampaign(Long id) {
        logger.info("Deleting campaign with id '{}'.", id);
        Optional<Campaign> optionalCampaign = this.campaignRepository.findByIdAndDeletedFalse(id);
        if (optionalCampaign.isPresent()) {
            Campaign campaign = optionalCampaign.get();
            campaign.setDeleted(true);
            this.campaignRepository.save(campaign);
            logger.debug("Campaign with ID '{}' has been marked as deleted.", id);
            return this.modelMapperService.forResponse().map(campaign, GetAllCampaignsResponse.class);
        } else {
            logger.warn("Campaign with ID '{}' not found for deletion.", id);
            throw new CampaignNotFoundException("Campaign not found");
        }
    }

    /**
     * Marks all campaigns as deleted.
     *
     * @throws CampaignNotFoundException if no campaigns are found to mark as deleted.
     */
    @Override
    public void deleteAllCampaigns() {
        logger.info("Deleting all campaigns.");
        List<Campaign> campaigns = this.campaignRepository.findAll();
        if (campaigns.isEmpty()) {
            logger.warn("No campaigns found to delete.");
            throw new CampaignNotFoundException("Campaign not found");
        }
        campaigns.forEach(campaign -> campaign.setDeleted(true));
        this.campaignRepository.saveAll(campaigns);
        logger.debug("All campaigns have been marked as deleted.");
    }
}
