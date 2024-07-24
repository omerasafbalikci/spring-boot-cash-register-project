package com.toyota.salesservice.resource;

import com.toyota.salesservice.dto.requests.CreateCampaignRequest;
import com.toyota.salesservice.dto.requests.UpdateCampaignRequest;
import com.toyota.salesservice.dto.responses.GetAllCampaignsResponse;
import com.toyota.salesservice.service.abstracts.CampaignService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing campaigns.
 */

@RestController
@RequestMapping("/api/campaigns")
@AllArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    /**
     * Retrieves a paginated and filtered list of campaigns.
     *
     * @param page the page number to retrieve, default is 0
     * @param size the size of the page to retrieve, default is 3
     * @param sort the sorting criteria, default is "id,asc"
     * @param id the ID filter, default is an empty value
     * @param campaignNumber the campaign number filter, default is an empty value
     * @param name the name filter, default is an empty value
     * @param campaignCategory the campaign category filter, default is an empty value
     * @param campaignKey the campaign key filter, default is an empty value
     * @param state the state filter, default is an empty value
     * @param createdBy the creator filter, default is an empty value
     * @return a ResponseEntity containing the filtered campaigns and pagination details
     */
    @GetMapping("/get-all")
    public ResponseEntity<Map<String, Object>> getCampaignsFiltered(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(defaultValue = "") Long id,
            @RequestParam(defaultValue = "") String campaignNumber,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String campaignCategory,
            @RequestParam(defaultValue = "") String campaignKey,
            @RequestParam(defaultValue = "") Boolean state,
            @RequestParam(defaultValue = "") String createdBy) {
        Map<String, Object> response = this.campaignService.getCampaignsFiltered(page, size, sort, id, campaignNumber, name, campaignCategory, campaignKey, state, createdBy);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Adds a new campaign.
     *
     * @param createCampaignRequest the request body containing the new campaign details
     * @return a ResponseEntity containing the created campaign details
     */
    @PostMapping("/add")
    public ResponseEntity<GetAllCampaignsResponse> addCampaign(@RequestBody() @Valid CreateCampaignRequest createCampaignRequest) {
        GetAllCampaignsResponse response = this.campaignService.addCampaign(createCampaignRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing campaign.
     *
     * @param updateCampaignRequest the request body containing the updated campaign details
     * @return a ResponseEntity containing the updated campaign details
     */
    @PutMapping("/update")
    public ResponseEntity<GetAllCampaignsResponse> updateCampaign(@RequestBody() @Valid UpdateCampaignRequest updateCampaignRequest) {
        GetAllCampaignsResponse response = this.campaignService.updateCampaign(updateCampaignRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft deletes a campaign by its ID.
     *
     * @param id the ID of the campaign to be deleted
     * @return a ResponseEntity containing the deleted campaign's details
     */
    @DeleteMapping("/delete")
    public ResponseEntity<GetAllCampaignsResponse> deleteCampaign(@RequestParam() Long id) {
        GetAllCampaignsResponse response = this.campaignService.deleteCampaign(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft deletes all campaigns by marking them as deleted.
     *
     * @return a success message indicating all campaigns have been marked as deleted
     */
    @DeleteMapping("/delete-all")
    public String deleteAllCampaign() {
        this.campaignService.deleteAllCampaigns();
        return "Deletion successful!";
    }
}
