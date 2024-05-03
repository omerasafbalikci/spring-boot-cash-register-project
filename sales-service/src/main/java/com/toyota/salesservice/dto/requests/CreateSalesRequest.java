package com.toyota.salesservice.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSalesRequest {
    @NotNull(message = "Created by must not be null")
    @NotBlank(message = "Created by must not be blank")
    private String createdBy;
    @NotNull(message = "Payment type must not be null")
    @NotBlank(message = "Payment type must not be blank")
    private String paymentType;
    @NotNull(message = "Unit price must not be null")
    @Min(value = 0, message = "Money must be greater than or equal to 0")
    private Double money;
    @NotEmpty(message = "Sales items list must not be empty")
    private List<CreateSalesItemsRequest> createSalesItemsRequests;
}
