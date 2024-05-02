package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import com.toyota.salesservice.dto.requests.UpdateCampaignRequest;
import com.toyota.salesservice.dto.responses.GetAllCampaignsResponse;

import java.util.List;

public interface CampaignService {
    List<GetAllCampaignsResponse> getAllCampaigns();
    GetAllCampaignsResponse getCampaignByCampaignNumber(String campaignNumber);
    GetAllCampaignsResponse addCampaign(CreateCampaignRequest createCampaignRequest);
    GetAllCampaignsResponse updateCampaign(UpdateCampaignRequest updateCampaignRequest);
    GetAllCampaignsResponse deleteCampaign(Long id);
    void deleteAllCampaigns();
}
