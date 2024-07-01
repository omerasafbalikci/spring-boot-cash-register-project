package com.toyota.salesservice.dto.requests;

import com.toyota.salesservice.domain.CampaignType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to update campaign.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCampaignRequest {
    @NotNull(message = "Id must not be null")
    private Long id;
    private String name;
    private CampaignType campaignTypes;
    private String campaignKey;
    private Boolean state;
    @NotNull(message = "Created by must not be null")
    @NotBlank(message = "Created by must not be blank")
    private String createdBy;
}
