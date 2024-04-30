package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import com.toyota.salesservice.dto.responses.GetAllCampaignsResponse;

import java.util.TreeMap;

public interface CampaignService {
    TreeMap<String, Object> getAllCampaignsPage(int page, int size, String[] sort);
    GetAllCampaignsResponse addCampaign(CreateCampaignRequest createCampaignRequest);
    GetAllCampaignsResponse updateCampaign(Long id, CreateCampaignRequest createCampaignRequest);
    GetAllCampaignsResponse deleteCampaign(Long id);
}
