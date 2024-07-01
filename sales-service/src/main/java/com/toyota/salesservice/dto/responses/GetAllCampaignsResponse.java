package com.toyota.salesservice.dto.responses;

import com.toyota.salesservice.domain.CampaignType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for campaign used as response.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllCampaignsResponse {
    private Long id;
    private String campaignNumber;
    private String name;
    private CampaignType campaignTypes;
    private String campaignKey;
    private Integer campaignType;
    private Boolean state;
    private String createdBy;
    private LocalDateTime updatedAt;
}
