package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import com.toyota.salesservice.dto.requests.UpdateCampaignRequest;
import com.toyota.salesservice.dto.responses.GetAllCampaignsResponse;

import java.util.List;

/**
 * Interface for campaign's service class.
 */

public interface CampaignService {
    /**
     * Retrieves all campaigns.
     *
     * @return a list of responses containing campaign details
     */
    List<GetAllCampaignsResponse> getAllCampaigns();

    /**
     * Retrieves a campaign by its campaign number.
     *
     * @param campaignNumber the campaign number
     * @return the response containing campaign details
     */
    GetAllCampaignsResponse getCampaignByCampaignNumber(String campaignNumber);

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
     * Deletes a campaign by its ID.
     *
     * @param id the ID of the campaign to delete
     * @return the response containing the deleted campaign details
     */
    GetAllCampaignsResponse deleteCampaign(Long id);

    /**
     * Deletes all campaigns.
     */
    void deleteAllCampaigns();
}
