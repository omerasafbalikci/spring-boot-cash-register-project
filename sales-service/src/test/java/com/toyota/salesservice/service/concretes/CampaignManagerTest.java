package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import com.toyota.salesservice.dto.requests.UpdateCampaignRequest;
import com.toyota.salesservice.dto.responses.GetAllCampaignsResponse;
import com.toyota.salesservice.service.rules.CampaignBusinessRules;
import com.toyota.salesservice.utilities.exceptions.CampaignAlreadyExistsException;
import com.toyota.salesservice.utilities.exceptions.CampaignNotFoundException;
import com.toyota.salesservice.utilities.mappers.ModelMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampaignManagerTest {
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private ModelMapperService modelMapperService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CampaignBusinessRules campaignBusinessRules;
    private CampaignManager campaignManager;

    @BeforeEach
    void setUp() {
        modelMapperService = mock(ModelMapperService.class);
        campaignManager = new CampaignManager(campaignRepository, modelMapperService, campaignBusinessRules);
    }

    @Test
    void getAllCampaigns_shouldReturnAllCampaigns() {
        List<Campaign> campaigns = List.of(new Campaign(), new Campaign());
        when(campaignRepository.findAll()).thenReturn(campaigns);
        when(modelMapper.map(any(Campaign.class), eq(GetAllCampaignsResponse.class))).thenReturn(new GetAllCampaignsResponse());
        when(modelMapperService.forResponse()).thenReturn(modelMapper);

        List<GetAllCampaignsResponse> responses = campaignManager.getAllCampaigns();

        assertEquals(2, responses.size());
        verify(campaignRepository, times(1)).findAll();
    }

    @Test
    void getCampaignByCampaignNumber_shouldReturnCampaign() {
        String campaignNumber = "12345678";
        Campaign campaign = new Campaign();
        campaign.setCampaignNumber(campaignNumber);
        when(campaignRepository.findByCampaignNumber(campaignNumber)).thenReturn(Optional.of(campaign));
        when(modelMapper.map(any(Campaign.class), eq(GetAllCampaignsResponse.class))).thenReturn(new GetAllCampaignsResponse());
        when(modelMapperService.forResponse()).thenReturn(modelMapper);

        GetAllCampaignsResponse response = campaignManager.getCampaignByCampaignNumber(campaignNumber);

        assertNotNull(response);
        verify(campaignRepository, times(1)).findByCampaignNumber(campaignNumber);
    }

    @Test
    void getCampaignByCampaignNumber_shouldThrowCampaignNotFoundException() {
        String campaignNumber = "12345678";
        when(campaignRepository.findByCampaignNumber(campaignNumber)).thenReturn(Optional.empty());

        assertThrows(CampaignNotFoundException.class, () -> campaignManager.getCampaignByCampaignNumber(campaignNumber));
    }

    @Test
    void addCampaign_shouldAddNewCampaign() {
        CreateCampaignRequest createCampaignRequest = new CreateCampaignRequest();
        createCampaignRequest.setName("New Campaign");
        createCampaignRequest.setBuyPay("1,2");

        Campaign campaign = new Campaign();
        campaign.setName("New Campaign");
        campaign.setCampaignNumber(UUID.randomUUID().toString().substring(0, 8));
        campaign.setUpdatedAt(LocalDateTime.now());

        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapperService.forRequest()).thenReturn(modelMapper);
        when(campaignRepository.existsByNameIgnoreCase(createCampaignRequest.getName())).thenReturn(false);
        when(modelMapper.map(any(CreateCampaignRequest.class), eq(Campaign.class))).thenReturn(campaign);
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
        when(modelMapper.map(any(Campaign.class), eq(GetAllCampaignsResponse.class))).thenReturn(new GetAllCampaignsResponse());

        GetAllCampaignsResponse response = campaignManager.addCampaign(createCampaignRequest);

        assertNotNull(response);
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void addCampaign_shouldThrowCampaignAlreadyExistsException() {
        CreateCampaignRequest createCampaignRequest = new CreateCampaignRequest();
        createCampaignRequest.setName("Existing Campaign");

        when(campaignRepository.existsByNameIgnoreCase(createCampaignRequest.getName())).thenReturn(true);

        assertThrows(CampaignAlreadyExistsException.class, () -> campaignManager.addCampaign(createCampaignRequest));
    }

    @Test
    void updateCampaign_shouldUpdateExistingCampaign() {
        UpdateCampaignRequest updateCampaignRequest = new UpdateCampaignRequest();
        updateCampaignRequest.setId(1L);
        updateCampaignRequest.setName("Updated Campaign");

        Campaign existingCampaign = new Campaign();
        existingCampaign.setId(1L);
        existingCampaign.setName("Existing Campaign");

        Campaign updatedCampaign = new Campaign();
        updatedCampaign.setId(1L);
        updatedCampaign.setName("Updated Campaign");

        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapperService.forRequest()).thenReturn(modelMapper);
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(existingCampaign));
        when(modelMapper.map(any(UpdateCampaignRequest.class), eq(Campaign.class))).thenReturn(updatedCampaign);
        when(campaignRepository.save(any(Campaign.class))).thenReturn(updatedCampaign);
        when(modelMapper.map(any(Campaign.class), eq(GetAllCampaignsResponse.class))).thenReturn(new GetAllCampaignsResponse());

        GetAllCampaignsResponse response = campaignManager.updateCampaign(updateCampaignRequest);

        assertNotNull(response);
        verify(campaignRepository, times(1)).save(updatedCampaign);
    }

    @Test
    void updateCampaign_shouldThrowCampaignNotFoundException() {
        UpdateCampaignRequest updateCampaignRequest = new UpdateCampaignRequest();
        updateCampaignRequest.setId(1L);

        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CampaignNotFoundException.class, () -> campaignManager.updateCampaign(updateCampaignRequest));
    }

    @Test
    void deleteCampaign_shouldDeleteExistingCampaign() {
        Campaign campaign = new Campaign();
        campaign.setId(1L);

        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(modelMapperService.forResponse().map(any(Campaign.class), eq(GetAllCampaignsResponse.class))).thenReturn(new GetAllCampaignsResponse());

        GetAllCampaignsResponse response = campaignManager.deleteCampaign(1L);

        assertNotNull(response);
        verify(campaignRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCampaign_shouldThrowCampaignNotFoundException() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CampaignNotFoundException.class, () -> campaignManager.deleteCampaign(1L));
    }

    @Test
    void deleteAllCampaigns_shouldDeleteAllCampaigns() {
        List<Campaign> campaigns = List.of(new Campaign(), new Campaign());
        when(campaignRepository.findAll()).thenReturn(campaigns);

        campaignManager.deleteAllCampaigns();

        verify(campaignRepository, times(1)).deleteAll();
    }

    @Test
    void deleteAllCampaigns_shouldThrowCampaignNotFoundException() {
        when(campaignRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(CampaignNotFoundException.class, () -> campaignManager.deleteAllCampaigns());
    }
}
