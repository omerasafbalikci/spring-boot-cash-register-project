package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import com.toyota.salesservice.dto.requests.UpdateCampaignRequest;
import com.toyota.salesservice.dto.responses.GetAllCampaignsResponse;

import java.util.Map;

/**
 * Interface for campaign's service class.
 */

public interface CampaignService {
    /**
     * Fetches a paginated and filtered list of campaigns.
     *
     * @param page             the page number to retrieve
     * @param size             the size of the page to retrieve
     * @param sort             the sorting criteria
     * @param id               the ID filter
     * @param campaignNumber   the campaign number filter
     * @param name             the name filter
     * @param campaignCategory the campaign category filter
     * @param campaignKey      the campaign key filter
     * @param state            the state filter
     * @param createdBy        the creator filter
     * @return a map containing the filtered campaigns and pagination details
     */
    Map<String, Object> getCampaignsFiltered(int page, int size, String[] sort, Long id, String campaignNumber, String name, String campaignCategory,
                                             String campaignKey, Boolean state, String createdBy);

    /**
     * Adds a new campaign.
     *
     * @param createCampaignRequest the request containing details for creating the campaign
     * @return the response containing the created campaign details
     */
    GetAllCampaignsResponse addCampaign(CreateCampaignRequest createCampaignRequest);

    /**
     * Updates an existing campaign.
     *
     * @param updateCampaignRequest the request containing details for updating the campaign
     * @return the response containing the updated campaign details
     */
    GetAllCampaignsResponse updateCampaign(UpdateCampaignRequest updateCampaignRequest);

    /**
     * Marks a campaign as deleted by its ID.
     *
     * @param id the ID of the campaign to be marked as deleted.
     * @return a {@link GetAllCampaignsResponse} object representing the campaign that was marked as deleted.
     */
    GetAllCampaignsResponse deleteCampaign(Long id);

    /**
     * Marks all campaigns as deleted.
     */
    void deleteAllCampaigns();
}
