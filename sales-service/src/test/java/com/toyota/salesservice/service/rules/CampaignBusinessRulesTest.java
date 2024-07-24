package com.toyota.salesservice.service.rules;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.domain.CampaignType;
import com.toyota.salesservice.utilities.exceptions.CampaignAlreadyExistsException;
import com.toyota.salesservice.utilities.exceptions.CampaignDetailsAreIncorrectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampaignBusinessRulesTest {
    @Mock
    private CampaignRepository campaignRepository;
    @InjectMocks
    private CampaignBusinessRules campaignBusinessRules;

    @BeforeEach
    void setUp() {
        campaignBusinessRules = new CampaignBusinessRules(campaignRepository);
    }

    @Test
    void checkCampaignDetails_whenCampaignKeyIsNull_shouldThrowException() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setCampaignCategory(CampaignType.BUYPAY);

        // Act & Assert
        CampaignDetailsAreIncorrectException exception = assertThrows(CampaignDetailsAreIncorrectException.class, () -> campaignBusinessRules.checkCampaignDetails(campaign));

        assertEquals("Campaign key not entered", exception.getMessage());
    }

    @Test
    void checkCampaignDetails_whenCampaignCategoryIsNull_shouldThrowException() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setCampaignKey("70");

        // Act & Assert
        CampaignDetailsAreIncorrectException exception = assertThrows(CampaignDetailsAreIncorrectException.class, () -> campaignBusinessRules.checkCampaignDetails(campaign));

        assertEquals("Campaign category not entered", exception.getMessage());
    }

    @Test
    void checkCampaignDetails_whenBuyPayEntryIsIncorrect_shouldThrowException() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setCampaignCategory(CampaignType.BUYPAY);
        campaign.setCampaignKey("3-2");

        // Act & Assert
        CampaignDetailsAreIncorrectException exception = assertThrows(CampaignDetailsAreIncorrectException.class, () -> campaignBusinessRules.checkCampaignDetails(campaign));

        assertEquals("Incorrect buy-pay entry. Please enter buyPay in the format 'integer,integer'. For example, '3,2'.", exception.getMessage());
    }

    @Test
    void checkCampaignDetails_whenBuyPayValuesAreIncorrect_shouldThrowException() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setCampaignCategory(CampaignType.BUYPAY);
        campaign.setCampaignKey("2,3");

        // Act & Assert
        CampaignDetailsAreIncorrectException exception = assertThrows(CampaignDetailsAreIncorrectException.class, () -> campaignBusinessRules.checkCampaignDetails(campaign));

        assertEquals("Incorrect buy-pay entry. 'Buy' value must be greater than 'Pay' value.", exception.getMessage());
    }

    @Test
    void checkCampaignDetails_whenPercentEntryIsIncorrect_shouldThrowException() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setCampaignCategory(CampaignType.PERCENT);
        campaign.setCampaignKey("150");

        // Act & Assert
        CampaignDetailsAreIncorrectException exception = assertThrows(CampaignDetailsAreIncorrectException.class, () -> campaignBusinessRules.checkCampaignDetails(campaign));

        assertEquals("Incorrect percent entry. Value must be between 0 and 100.", exception.getMessage());
    }

    @Test
    void checkCampaignDetails_whenMoneyDiscountEntryIsIncorrect_shouldThrowException() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setCampaignCategory(CampaignType.MONEYDISCOUNT);
        campaign.setCampaignKey("0");

        // Act & Assert
        CampaignDetailsAreIncorrectException exception = assertThrows(CampaignDetailsAreIncorrectException.class, () -> campaignBusinessRules.checkCampaignDetails(campaign));

        assertEquals("Incorrect money discount entry. Value must be greater than 0.", exception.getMessage());
    }

    @Test
    void checkUpdate_whenFieldsAreNull_shouldCopyExistingFields() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);

        Campaign existingCampaign = new Campaign();
        existingCampaign.setId(1L);
        existingCampaign.setName("Existing Campaign");
        existingCampaign.setState(true);
        existingCampaign.setCampaignKey("70");
        existingCampaign.setCampaignCategory(CampaignType.PERCENT);
        existingCampaign.setCampaignType(1);

        // Act
        campaignBusinessRules.checkUpdate(campaign, existingCampaign);

        // Assert
        assertEquals(existingCampaign.getName(), campaign.getName());
        assertEquals(existingCampaign.getState(), campaign.getState());
        assertEquals(existingCampaign.getCampaignKey(), campaign.getCampaignKey());
        assertEquals(existingCampaign.getCampaignCategory(), campaign.getCampaignCategory());
        assertEquals(existingCampaign.getCampaignType(), campaign.getCampaignType());
    }

    @Test
    void checkUpdate_whenCampaignNameExistsAndIsDifferent_shouldThrowException() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("New Campaign");

        Campaign existingCampaign = new Campaign();
        existingCampaign.setId(1L);
        existingCampaign.setName("Existing Campaign");

        when(campaignRepository.existsByNameIgnoreCaseAndDeletedIsFalse(campaign.getName())).thenReturn(true);

        // Act & Assert
        CampaignAlreadyExistsException exception = assertThrows(CampaignAlreadyExistsException.class, () -> campaignBusinessRules.checkUpdate(campaign, existingCampaign));

        assertEquals("Campaign already exists", exception.getMessage());
        verify(campaignRepository, times(1)).existsByNameIgnoreCaseAndDeletedIsFalse(campaign.getName());
    }

    @Test
    void checkUpdate_whenCampaignNameExistsAndIsSame_shouldNotThrowException() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("Existing Campaign");

        Campaign existingCampaign = new Campaign();
        existingCampaign.setId(1L);
        existingCampaign.setName("Existing Campaign");

        when(campaignRepository.existsByNameIgnoreCaseAndDeletedIsFalse(campaign.getName())).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> campaignBusinessRules.checkUpdate(campaign, existingCampaign));

        verify(campaignRepository, times(1)).existsByNameIgnoreCaseAndDeletedIsFalse(campaign.getName());
    }

    @Test
    void checkUpdate_whenCampaignNameDoesNotExist_shouldNotThrowException() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("New Campaign");

        Campaign existingCampaign = new Campaign();
        existingCampaign.setId(1L);
        existingCampaign.setName("Existing Campaign");

        when(campaignRepository.existsByNameIgnoreCaseAndDeletedIsFalse(campaign.getName())).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> campaignBusinessRules.checkUpdate(campaign, existingCampaign));

        verify(campaignRepository, times(1)).existsByNameIgnoreCaseAndDeletedIsFalse(campaign.getName());
    }
}
