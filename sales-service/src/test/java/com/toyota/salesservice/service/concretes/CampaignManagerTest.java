package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.CampaignRepository;
import com.toyota.salesservice.dao.CampaignSpecification;
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
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

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
    void getCampaignsFiltered_asc() {
        // Given
        int page = 0;
        int size = 3;
        String[] sort = {"id,asc"};

        Campaign campaign1 = new Campaign(1L, "1234567890123", "Campaign1", CampaignType.BUYPAY, "3,2", 1, true, "Asaf", LocalDateTime.now(), null, false);
        Campaign campaign2 = new Campaign(2L, "1234567890124", "Campaign2", CampaignType.BUYPAY, "7,6", 1, true, "Asaf", LocalDateTime.now(), null, false);
        List<Campaign> campaignList = Arrays.asList(campaign1, campaign2);

        Page<Campaign> campaignPage = new PageImpl<>(campaignList, PageRequest.of(page, size, Sort.by(Sort.Order.asc("id"))), campaignList.size());

        // When
        when(campaignRepository.findAll(any(CampaignSpecification.class), any(Pageable.class))).thenReturn(campaignPage);
        when(modelMapper.map(any(Campaign.class), eq(GetAllCampaignsResponse.class)))
                .thenAnswer(invocation -> {
                    Campaign campaign = invocation.getArgument(0);
                    return new GetAllCampaignsResponse(campaign.getId(), campaign.getCampaignNumber(), campaign.getName(), campaign.getCampaignCategory(), campaign.getCampaignKey(), campaign.getCampaignType(), campaign.getState(), campaign.getCreatedBy(), LocalDateTime.now());
                });
        when(modelMapperService.forResponse()).thenReturn(modelMapper);

        Map<String, Object> response = campaignManager.getCampaignsFiltered(page, size, sort, null, null, null, null, null, null, null);

        // Then
        @SuppressWarnings("unchecked")
        List<GetAllCampaignsResponse> campaignsResponseList = (List<GetAllCampaignsResponse>) response.get("campaigns");

        assertEquals(2, campaignsResponseList.size());
        assertEquals(0, response.get("currentPage"));
        assertEquals(2L, response.get("totalItems"));
        assertEquals(1, response.get("totalPages"));
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

        when(campaignRepository.existsByNameIgnoreCaseAndDeletedIsFalse(request.getName())).thenReturn(false);
        when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapper.map(any(Campaign.class), eq(GetAllCampaignsResponse.class))).thenReturn(response);

        GetAllCampaignsResponse actualResponse = campaignManager.addCampaign(request);

        assertNotNull(actualResponse);
        assertEquals(request.getName(), actualResponse.getName());
        assertEquals(request.getCampaignCategory(), actualResponse.getCampaignCategory());

        verify(campaignRepository, times(1)).existsByNameIgnoreCaseAndDeletedIsFalse(request.getName());
        verify(campaignRepository, times(1)).save(any(Campaign.class));
        verify(modelMapperService, times(1)).forResponse();
        verify(modelMapper, times(1)).map(any(Campaign.class), eq(GetAllCampaignsResponse.class));
    }

    @Test
    void addCampaign_whenCampaignExists_shouldThrowException() {
        CreateCampaignRequest request = new CreateCampaignRequest();
        request.setName("Existing Campaign");

        when(campaignRepository.existsByNameIgnoreCaseAndDeletedIsFalse(request.getName())).thenReturn(true);

        CampaignAlreadyExistsException exception = assertThrows(CampaignAlreadyExistsException.class, () -> campaignManager.addCampaign(request));

        assertEquals("Campaign already exists", exception.getMessage());

        verify(campaignRepository, times(1)).existsByNameIgnoreCaseAndDeletedIsFalse(request.getName());
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

        when(campaignRepository.findByIdAndDeletedFalse(request.getId())).thenReturn(Optional.of(existingCampaign));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(updatedCampaign);
        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapper.map(any(Campaign.class), eq(GetAllCampaignsResponse.class))).thenReturn(response);

        GetAllCampaignsResponse actualResponse = campaignManager.updateCampaign(request);

        assertNotNull(actualResponse);
        assertEquals(request.getName(), actualResponse.getName());
        assertEquals(request.getCampaignCategory(), actualResponse.getCampaignCategory());

        verify(campaignRepository, times(1)).findByIdAndDeletedFalse(request.getId());
        verify(campaignRepository, times(1)).save(any(Campaign.class));
        verify(modelMapperService, times(1)).forResponse();
        verify(modelMapper, times(1)).map(any(Campaign.class), eq(GetAllCampaignsResponse.class));
    }

    @Test
    void updateCampaign_whenCampaignDoesNotExist_shouldThrowException() {
        UpdateCampaignRequest request = new UpdateCampaignRequest();
        request.setId(1L);

        when(campaignRepository.findByIdAndDeletedFalse(request.getId())).thenReturn(Optional.empty());

        CampaignNotFoundException exception = assertThrows(CampaignNotFoundException.class, () -> campaignManager.updateCampaign(request));

        assertEquals("Campaign not found", exception.getMessage());

        verify(campaignRepository, times(1)).findByIdAndDeletedFalse(request.getId());
        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void deleteCampaign_shouldDeleteExistingCampaign() {
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setDeleted(false);

        when(campaignRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(campaign));
        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapper.map(any(Campaign.class), eq(GetAllCampaignsResponse.class))).thenReturn(new GetAllCampaignsResponse());

        GetAllCampaignsResponse response = campaignManager.deleteCampaign(1L);

        assertNotNull(response);
        assertTrue(campaign.isDeleted());
        verify(campaignRepository, times(1)).save(campaign);
        verify(modelMapperService, times(1)).forResponse();
        verify(modelMapper, times(1)).map(any(Campaign.class), eq(GetAllCampaignsResponse.class));
    }

    @Test
    void deleteCampaign_shouldThrowCampaignNotFoundException() {
        when(campaignRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        CampaignNotFoundException exception = assertThrows(CampaignNotFoundException.class, () -> campaignManager.deleteCampaign(1L));

        assertEquals("Campaign not found", exception.getMessage());
        verify(campaignRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void deleteAllCampaigns_shouldDeleteAllCampaigns() {
        List<Campaign> campaigns = List.of(new Campaign(), new Campaign());
        campaigns.forEach(campaign -> campaign.setDeleted(false));

        when(campaignRepository.findAll()).thenReturn(campaigns);

        campaignManager.deleteAllCampaigns();

        campaigns.forEach(campaign -> assertTrue(campaign.isDeleted()));
        verify(campaignRepository, times(1)).saveAll(campaigns);
    }

    @Test
    void deleteAllCampaigns_shouldThrowCampaignNotFoundException() {
        when(campaignRepository.findAll()).thenReturn(Collections.emptyList());

        CampaignNotFoundException exception = assertThrows(CampaignNotFoundException.class, () -> campaignManager.deleteAllCampaigns());

        assertEquals("Campaign not found", exception.getMessage());
        verify(campaignRepository, times(1)).findAll();
    }
}
