package com.toyota.salesservice.service.rules;

import com.toyota.salesservice.domain.Campaign;

public class CampaignBusinessRules {
    public void checkUpdate(Campaign campaign, Campaign existingCampaign) {
        if (campaign.getName() == null) {
            campaign.setName(existingCampaign.getName());
        }
        if (campaign.getState() == null) {
            campaign.setState(existingCampaign.getState());
        }
    }
}
