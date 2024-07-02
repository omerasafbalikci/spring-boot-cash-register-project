package com.toyota.salesservice.dto.requests;

import com.toyota.salesservice.domain.CampaignType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for campaign used as input.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCampaignRequest {
    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotNull(message = "Campaign category must not be null")
    private CampaignType campaignCategory;
    @NotNull(message = "Campaign key must not be null")
    @NotBlank(message = "Campaign key must not be blank")
    private String campaignKey;
    @NotNull(message = "State must not be null")
    private Boolean state;
    @NotNull(message = "Created by must not be null")
    @NotBlank(message = "Created by must not be blank")
    private String createdBy;
}
