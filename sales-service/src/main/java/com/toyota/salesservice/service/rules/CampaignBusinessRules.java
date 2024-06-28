package com.toyota.salesservice.service.rules;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import com.toyota.salesservice.dto.requests.UpdateCampaignRequest;
import com.toyota.salesservice.service.abstracts.CampaignService;
import com.toyota.salesservice.utilities.exceptions.CampaignAlreadyExistsException;
import com.toyota.salesservice.utilities.exceptions.CampaignDetailsAreIncorrectException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Service for handling business rules related to campaigns.
 */

@Service
@AllArgsConstructor
public class CampaignBusinessRules {
    private final CampaignRepository campaignRepository;
    private final Logger logger = LogManager.getLogger(CampaignService.class);

    /**
     * Validates the details of a campaign, ensuring that the buy-pay entry is in the correct format.
     *
     * @param campaign the campaign to validate
     * @throws CampaignDetailsAreIncorrectException if the buy-pay entry is incorrect
     */
    public void checkCampaignDetails(Campaign campaign) {
        if (campaign.getBuyPay() != null) {
            Pattern pattern = Pattern.compile("^\\d+,\\d+$");
            if (!pattern.matcher(campaign.getBuyPay()).matches()) {
                logger.error("Incorrect buy-pay entry. Campaign ID: {}. Please enter buyPay in the format 'integer,integer'. For example, '3,2'.", campaign.getId());
                throw new CampaignDetailsAreIncorrectException("Incorrect buy-pay entry. Please enter buyPay in the format 'integer,integer'. For example, '3,2'.");
            }

            String[] parts = campaign.getBuyPay().split(",");
            int buyPayPartOne = Integer.parseInt(parts[0]);
            int buyPayPartTwo = Integer.parseInt(parts[1]);

            if (buyPayPartOne <= buyPayPartTwo) {
                logger.error("Incorrect buy-pay entry. Campaign ID: " + campaign.getId() + ". 'Buy' value must be greater than 'Pay' value.");
                throw new CampaignDetailsAreIncorrectException("Incorrect buy-pay entry. 'Buy' value must be greater than 'Pay' value.");
            }

            campaign.setBuyPayPartOne(buyPayPartOne);
            campaign.setBuyPayPartTwo(buyPayPartTwo);
        }
    }

    /**
     * Sets the campaign type based on the details provided in the create campaign request.
     *
     * @param campaign the campaign to update
     * @param createCampaignRequest the request containing the campaign details
     * @throws CampaignDetailsAreIncorrectException if no campaign details are provided
     */
    public void addCampaignType(Campaign campaign, CreateCampaignRequest createCampaignRequest) {
        if (createCampaignRequest.getBuyPay() == null && createCampaignRequest.getPercent() == null && createCampaignRequest.getMoneyDiscount() == null) {
            logger.error("Campaign details not entered. Campaign ID: {}.", campaign.getId());
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

    /**
     * Updates the campaign type based on the details provided in the update campaign request.
     *
     * @param campaign the campaign to update
     * @param updateCampaignRequest the request containing the updated campaign details
     * @throws CampaignDetailsAreIncorrectException if no campaign details are provided
     */
    public void updateCampaignType(Campaign campaign, UpdateCampaignRequest updateCampaignRequest) {
        if (updateCampaignRequest.getBuyPay() != null) {
            campaign.setCampaignType(1);
        } else if (updateCampaignRequest.getPercent() != null) {
            campaign.setCampaignType(2);
        } else if (updateCampaignRequest.getMoneyDiscount() != null) {
            campaign.setCampaignType(3);
        }
        if (campaign.getBuyPay() == null && campaign.getPercent() == null && campaign.getMoneyDiscount() == null) {
            logger.error("Campaign details not entered. Campaign ID: {}.", campaign.getId());
            throw new CampaignDetailsAreIncorrectException("Campaign details not entered");
        }
    }

    /**
     * Checks and updates the campaign with existing details if new details are not provided.
     * Also checks if the campaign name already exists.
     *
     * @param campaign the campaign to update
     * @param existingCampaign the existing campaign details
     * @throws CampaignAlreadyExistsException if a campaign with the same name already exists
     */
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
            logger.error("Campaign with the same name already exists. Campaign Name: {}. Campaign ID: {}.", campaign.getName(), campaign.getId());
            throw new CampaignAlreadyExistsException("Campaign already exists");
        }
    }
}
