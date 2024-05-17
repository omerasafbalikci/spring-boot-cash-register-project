package com.toyota.salesservice.service.rules;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import com.toyota.salesservice.dto.requests.UpdateCampaignRequest;
import com.toyota.salesservice.utilities.exceptions.CampaignAlreadyExistsException;
import com.toyota.salesservice.utilities.exceptions.CampaignDetailsAreIncorrectException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class CampaignBusinessRules {
    private final CampaignRepository campaignRepository;

    public void checkCampaignDetails(Campaign campaign) {
        if (campaign.getBuyPay() != null) {
            Pattern pattern = Pattern.compile("^\\d+,\\d+$");
            if (!pattern.matcher(campaign.getBuyPay()).matches()) {
                throw new CampaignDetailsAreIncorrectException("Incorrect buy-pay entry");
            }

            String[] parts = campaign.getBuyPay().split(",");
            int buyPayPartOne = Integer.parseInt(parts[0]);
            int buyPayPartTwo = Integer.parseInt(parts[1]);

            if (buyPayPartOne < 0 || buyPayPartTwo < 0) {
                throw new CampaignDetailsAreIncorrectException("Incorrect buy-pay entry");
            }

            campaign.setBuyPayPartOne(buyPayPartOne);
            campaign.setBuyPayPartTwo(buyPayPartTwo);
        }
    }

    public void addCampaignType(Campaign campaign, CreateCampaignRequest createCampaignRequest) {
        if (createCampaignRequest.getBuyPay() == null && createCampaignRequest.getPercent() == null && createCampaignRequest.getMoneyDiscount() == null) {
            throw new CampaignDetailsAreIncorrectException("Campaign details not entered");
        }
        if (createCampaignRequest.getBuyPay() != null) {
            campaign.setCampaignType(1);
        } else if (createCampaignRequest.getPercent() != null) {
            campaign.setCampaignType(2);
        } else {
            campaign.setCampaignType(3);
        }
    }

    public void updateCampaignType(Campaign campaign, UpdateCampaignRequest updateCampaignRequest) {
        if (updateCampaignRequest.getBuyPay() == null && updateCampaignRequest.getPercent() == null && updateCampaignRequest.getMoneyDiscount() == null) {
            throw new CampaignDetailsAreIncorrectException("Campaign details not entered");
        }
        if (updateCampaignRequest.getBuyPay() != null) {
            campaign.setCampaignType(1);
        } else if (updateCampaignRequest.getPercent() != null) {
            campaign.setCampaignType(2);
        } else {
            campaign.setCampaignType(3);
        }
    }

    public void checkUpdate(Campaign campaign, Campaign existingCampaign) {
        if (campaign.getName() == null) {
            campaign.setName(existingCampaign.getName());
        }
        if (campaign.getState() == null) {
            campaign.setState(existingCampaign.getState());
        }
        if (campaign.getBuyPay() == null) {
            campaign.setBuyPay(existingCampaign.getBuyPay());
        }
        if (campaign.getPercent() == null) {
            campaign.setPercent(existingCampaign.getPercent());
        }
        if (campaign.getMoneyDiscount() == null) {
            campaign.setMoneyDiscount(existingCampaign.getMoneyDiscount());
        }
        if (campaign.getCampaignType() == null) {
            campaign.setCampaignType(existingCampaign.getCampaignType());
        }

        if (this.campaignRepository.existsByNameIgnoreCase(campaign.getName()) && !existingCampaign.getName().equals(campaign.getName())) {
            throw new CampaignAlreadyExistsException("Campaign already exists");
        }
    }
}
