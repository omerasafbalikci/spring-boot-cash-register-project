package com.toyota.salesservice.service.rules;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.domain.CampaignType;
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
     * Validates the details of a campaign, ensuring that the campaign key is in the correct format
     * based on the campaign type.
     *
     * @param campaign the campaign to validate
     * @throws CampaignDetailsAreIncorrectException if the campaign key is incorrect based on the campaign type
     */
    public void checkCampaignDetails(Campaign campaign) {
        if ((campaign.getCampaignCategory() != null) && (campaign.getCampaignKey() == null)) {
            logger.warn("Campaign key not entered.");
            throw new CampaignDetailsAreIncorrectException("Campaign key not entered");
        }
        if ((campaign.getCampaignCategory() == null) && (campaign.getCampaignKey() != null)) {
            logger.warn("Campaign category not entered.");
            throw new CampaignDetailsAreIncorrectException("Campaign category not entered");
        }
        if (campaign.getCampaignCategory() == CampaignType.BUYPAY) {
            Pattern pattern = Pattern.compile("^\\d+,\\d+$");
            if (!pattern.matcher(campaign.getCampaignKey()).matches()) {
                logger.warn("Incorrect buy-pay entry. Campaign ID: {}. Please enter buyPay in the format 'integer,integer'. For example, '3,2'.", campaign.getId());
                throw new CampaignDetailsAreIncorrectException("Incorrect buy-pay entry. Please enter buyPay in the format 'integer,integer'. For example, '3,2'.");
            }

            String[] parts = campaign.getCampaignKey().split(",");
            int buyPayPartOne = Integer.parseInt(parts[0]);
            int buyPayPartTwo = Integer.parseInt(parts[1]);

            if (buyPayPartOne <= buyPayPartTwo) {
                logger.warn("Incorrect buy-pay entry. Campaign ID: {}. 'Buy' value must be greater than 'Pay' value.", campaign.getId());
                throw new CampaignDetailsAreIncorrectException("Incorrect buy-pay entry. 'Buy' value must be greater than 'Pay' value.");
            }
            campaign.setCampaignType(1);
        } else if (campaign.getCampaignCategory() == CampaignType.PERCENT) {
            try {
                int percentValue = Integer.parseInt(campaign.getCampaignKey());
                if (percentValue <= 0 || percentValue > 100) {
                    logger.warn("Incorrect percent entry. Campaign ID: {}. Value must be between 0 and 100.", campaign.getId());
                    throw new CampaignDetailsAreIncorrectException("Incorrect percent entry. Value must be between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid percent entry. Campaign ID: {}. Value must be an integer.", campaign.getId());
                throw new CampaignDetailsAreIncorrectException("Invalid percent entry. Value must be an integer.");
            }
            campaign.setCampaignType(2);
        } else if (campaign.getCampaignCategory() == CampaignType.MONEYDISCOUNT) {
            try {
                int moneyDiscountValue = Integer.parseInt(campaign.getCampaignKey());
                if (moneyDiscountValue <= 0) {
                    logger.warn("Incorrect money discount entry. Campaign ID: {}. Value must be greater than 0.", campaign.getId());
                    throw new CampaignDetailsAreIncorrectException("Incorrect money discount entry. Value must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid money discount entry. Campaign ID: {}. Value must be an integer.", campaign.getId());
                throw new CampaignDetailsAreIncorrectException("Invalid money discount entry. Value must be an integer.");
            }
            campaign.setCampaignType(3);
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
        if (campaign.getCampaignKey() == null) {
            campaign.setCampaignKey(existingCampaign.getCampaignKey());
        }
        if (campaign.getCampaignCategory() == null) {
            campaign.setCampaignCategory(existingCampaign.getCampaignCategory());
        }
        if (campaign.getCampaignType() == null) {
            campaign.setCampaignType(existingCampaign.getCampaignType());
        }

        if (this.campaignRepository.existsByNameIgnoreCaseAndDeletedIsFalse(campaign.getName()) && !existingCampaign.getName().equals(campaign.getName())) {
            logger.warn("Campaign with the same name already exists. Campaign Name: {}. Campaign ID: {}.", campaign.getName(), campaign.getId());
            throw new CampaignAlreadyExistsException("Campaign already exists");
        }
    }
}
