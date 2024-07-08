package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.domain.CampaignType;
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
    void addCampaign_whenCampaignDoesNotExist_shouldAddCampaign() {
        CreateCampaignRequest request = new CreateCampaignRequest();
        request.setName("New Campaign");
        request.setCampaignCategory(CampaignType.PERCENT);
        request.setCampaignKey("50");
        request.setState(true);
        request.setCreatedBy("Ömer Asaf Balıkçı");

        Campaign campaign = new Campaign();
        campaign.setName(request.getName());
        campaign.setCampaignCategory(request.getCampaignCategory());
        campaign.setCampaignKey(request.getCampaignKey());
        campaign.setState(request.getState());
        campaign.setCreatedBy(request.getCreatedBy());
        campaign.setCampaignNumber(UUID.randomUUID().toString().substring(0, 8));
        campaign.setUpdatedAt(LocalDateTime.now());

        GetAllCampaignsResponse response = new GetAllCampaignsResponse();
        response.setName(campaign.getName());
        response.setCampaignCategory(campaign.getCampaignCategory());

        when(campaignRepository.existsByNameIgnoreCase(request.getName())).thenReturn(false);
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapper.map(any(Campaign.class), eq(GetAllCampaignsResponse.class))).thenReturn(response);

        GetAllCampaignsResponse actualResponse = campaignManager.addCampaign(request);

        assertNotNull(actualResponse);
        assertEquals(request.getName(), actualResponse.getName());
        assertEquals(request.getCampaignCategory(), actualResponse.getCampaignCategory());

        verify(campaignRepository, times(1)).existsByNameIgnoreCase(request.getName());
        verify(campaignRepository, times(1)).save(any(Campaign.class));
        verify(modelMapperService, times(1)).forResponse();
        verify(modelMapper, times(1)).map(any(Campaign.class), eq(GetAllCampaignsResponse.class));
    }

    @Test
    void addCampaign_whenCampaignExists_shouldThrowException() {
        CreateCampaignRequest request = new CreateCampaignRequest();
        request.setName("Existing Campaign");

        when(campaignRepository.existsByNameIgnoreCase(request.getName())).thenReturn(true);

        CampaignAlreadyExistsException exception = assertThrows(CampaignAlreadyExistsException.class, () -> campaignManager.addCampaign(request));

        assertEquals("Campaign already exists", exception.getMessage());

        verify(campaignRepository, times(1)).existsByNameIgnoreCase(request.getName());
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void updateCampaign_whenCampaignExists_shouldUpdateCampaign() {
        UpdateCampaignRequest request = new UpdateCampaignRequest();
        request.setId(1L);
        request.setName("Updated Campaign");
        request.setCampaignCategory(CampaignType.PERCENT);
        request.setCampaignKey("50");
        request.setState(true);
        request.setCreatedBy("Ömer Asaf Balıkçı");

        Campaign existingCampaign = new Campaign();
        existingCampaign.setId(1L);
        existingCampaign.setName("Existing Campaign");
        existingCampaign.setCampaignCategory(CampaignType.PERCENT);
        existingCampaign.setCampaignKey("70");
        existingCampaign.setState(true);
        existingCampaign.setCreatedBy("Ömer Asaf Balıkçı");
        existingCampaign.setCampaignNumber("12345678");
        existingCampaign.setUpdatedAt(LocalDateTime.now());

        Campaign updatedCampaign = new Campaign();
        updatedCampaign.setId(request.getId());
        updatedCampaign.setName(request.getName());
        updatedCampaign.setCampaignCategory(request.getCampaignCategory());
        updatedCampaign.setCampaignKey(request.getCampaignKey());
        updatedCampaign.setState(request.getState());
        updatedCampaign.setCreatedBy(request.getCreatedBy());
        updatedCampaign.setCampaignNumber(existingCampaign.getCampaignNumber());
        updatedCampaign.setUpdatedAt(LocalDateTime.now());

        GetAllCampaignsResponse response = new GetAllCampaignsResponse();
        response.setName(updatedCampaign.getName());
        response.setCampaignCategory(updatedCampaign.getCampaignCategory());

        when(campaignRepository.findById(request.getId())).thenReturn(Optional.of(existingCampaign));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(updatedCampaign);
        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapper.map(any(Campaign.class), eq(GetAllCampaignsResponse.class))).thenReturn(response);

        GetAllCampaignsResponse actualResponse = campaignManager.updateCampaign(request);

        assertNotNull(actualResponse);
        assertEquals(request.getName(), actualResponse.getName());
        assertEquals(request.getCampaignCategory(), actualResponse.getCampaignCategory());

        verify(campaignRepository, times(1)).findById(request.getId());
        verify(campaignRepository, times(1)).save(any(Campaign.class));
        verify(modelMapperService, times(1)).forResponse();
        verify(modelMapper, times(1)).map(any(Campaign.class), eq(GetAllCampaignsResponse.class));
    }

    @Test
    void updateCampaign_whenCampaignDoesNotExist_shouldThrowException() {
        UpdateCampaignRequest request = new UpdateCampaignRequest();
        request.setId(1L);

        when(campaignRepository.findById(request.getId())).thenReturn(Optional.empty());

        CampaignNotFoundException exception = assertThrows(CampaignNotFoundException.class, () -> campaignManager.updateCampaign(request));

        assertEquals("Campaign not found", exception.getMessage());

        verify(campaignRepository, times(1)).findById(request.getId());
        verify(campaignRepository, never()).save(any(Campaign.class));
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
