package com.toyota.salesservice.dto.responses;

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
    private String buyPay;
    private Integer percent;
    private Integer moneyDiscount;
    private Integer campaignType;
    private Boolean state;
    private String createdBy;
    private LocalDateTime updatedAt;
}
