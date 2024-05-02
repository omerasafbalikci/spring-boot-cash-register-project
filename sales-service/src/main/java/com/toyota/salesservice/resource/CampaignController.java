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

@RestController
@RequestMapping("/api/campaigns")
@AllArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

    @GetMapping()
    public ResponseEntity<List<GetAllCampaignsResponse>> getAllCampaigns() {
        List<GetAllCampaignsResponse> responses = this.campaignService.getAllCampaigns();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/categorynumber")
    public ResponseEntity<GetAllCampaignsResponse> getCampaignByCampaignNumber(@RequestParam String campaignNumber) {
        GetAllCampaignsResponse response = this.campaignService.getCampaignByCampaignNumber(campaignNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<GetAllCampaignsResponse> addCampaign(@RequestBody() @Valid CreateCampaignRequest createCampaignRequest) {
        GetAllCampaignsResponse response = this.campaignService.addCampaign(createCampaignRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<GetAllCampaignsResponse> updateCampaign(@RequestBody() @Valid UpdateCampaignRequest updateCampaignRequest) {
        GetAllCampaignsResponse response = this.campaignService.updateCampaign(updateCampaignRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<GetAllCampaignsResponse> deleteCampaign(@RequestParam() Long id) {
        GetAllCampaignsResponse response = this.campaignService.deleteCampaign(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteall")
    public String deleteAllCampaign() {
        this.campaignService.deleteAllCampaigns();
        return "Deletion successful!";
    }
}
