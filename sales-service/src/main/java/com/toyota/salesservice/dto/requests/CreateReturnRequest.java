package com.toyota.salesservice.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for return used as input.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReturnRequest {
    @NotNull(message = "Sales number must not be null")
    @NotBlank(message = "Sales number must not be blank")
    private String salesNumber;
    @NotNull(message = "Barcode number must not be null")
    @NotBlank(message = "Barcode number must not be blank")
    private String barcodeNumber;
    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be greater than or equal to 1")
    private Integer quantity;
    private LocalDateTime returnDate = LocalDateTime.now();
}
