package com.toyota.salesservice.dto.requests;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCampaignRequest {
    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    private String name;
    private String buyPay;
    private String percent;
    private String moneyDiscount;
    @NotNull(message = "State must not be null")
    private Boolean state;
    @NotNull(message = "Created by must not be null")
    @NotBlank(message = "Created by must not be blank")
    private String createdBy;
}
