package com.toyota.salesservice.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSalesRequest {
    private String description;
    @NotNull
    private Double price;
    private Long campaignId;
    @NotNull
    private Long cartId;
}
