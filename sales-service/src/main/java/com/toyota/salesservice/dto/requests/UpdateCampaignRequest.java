package com.toyota.salesservice.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCampaignRequest {
    @NotNull(message = "Id must not be null")
    private Long id;
    private String name;
    private String buyPay;
    private String percent;
    private String moneyDiscount;
    private Boolean state;
    @NotNull(message = "Created by must not be null")
    @NotBlank(message = "Created by must not be blank")
    private String createdBy;
}
