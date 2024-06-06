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

import java.util.List;

/**
 * REST controller for managing campaigns.
 */

@RestController
@RequestMapping("/api/campaigns")
@AllArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    /**
     * Retrieves all campaigns.
     *
     * @return a ResponseEntity containing a list of all campaigns
     */
    @GetMapping("/get-all")
    public ResponseEntity<List<GetAllCampaignsResponse>> getAllCampaigns() {
        List<GetAllCampaignsResponse> responses = this.campaignService.getAllCampaigns();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /**
     * Retrieves a campaign by its campaign number.
     *
     * @param campaignNumber the campaign number to search for
     * @return a ResponseEntity containing the campaign details
     */
    @GetMapping("/campaign-number")
    public ResponseEntity<GetAllCampaignsResponse> getCampaignByCampaignNumber(@RequestParam String campaignNumber) {
        GetAllCampaignsResponse response = this.campaignService.getCampaignByCampaignNumber(campaignNumber);
        return ResponseEntity.ok(response);
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
     * Deletes a campaign by its ID.
     *
     * @param id the ID of the campaign to delete
     * @return a ResponseEntity containing the details of the deleted campaign
     */
    @DeleteMapping("/delete")
    public ResponseEntity<GetAllCampaignsResponse> deleteCampaign(@RequestParam() Long id) {
        GetAllCampaignsResponse response = this.campaignService.deleteCampaign(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes all campaigns.
     *
     * @return a string message indicating that the deletion was successful
     */
    @DeleteMapping("/delete-all")
    public String deleteAllCampaign() {
        this.campaignService.deleteAllCampaigns();
        return "Deletion successful!";
    }
}
