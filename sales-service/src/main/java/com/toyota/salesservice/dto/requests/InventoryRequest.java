package com.toyota.salesservice.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for inventory request.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryRequest {
    @NotNull(message = "Barcode number must not be null")
    @NotBlank(message = "Barcode number must not be blank")
    private String barcodeNumber;
    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be greater than or equal to 1")
    private Integer quantity;
}
