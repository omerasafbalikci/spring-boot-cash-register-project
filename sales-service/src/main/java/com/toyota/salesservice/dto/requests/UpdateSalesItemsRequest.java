package com.toyota.salesservice.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSalesItemsRequest {
    private String skuCode;
    @Min(value = 1, message = "Quantity must be greater than or equal to 1")
    private Integer quantity;
    private Long campaignId;
}
