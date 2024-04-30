package com.toyota.salesservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllCampaignsResponse {
    private Long id;
    private String campaignNumber;
    private String name;
    private Boolean state;
}
