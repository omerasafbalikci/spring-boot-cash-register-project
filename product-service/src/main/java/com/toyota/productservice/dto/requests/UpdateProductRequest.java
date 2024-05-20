package com.toyota.productservice.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to update product.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
    @NotNull(message = "Id must not be null")
    private Long id;
    private String name;
    private String description;
    @Min(value = 1, message = "Quantity must be greater than or equal to 1")
    private Integer quantity;
    @Min(value = 0, message = "Unit price must be greater than or equal to 0")
    private Double unitPrice;
    private Boolean state;
    private String imageUrl;
    @NotNull(message = "Created by must not be null")
    @NotBlank(message = "Created by must not be blank")
    private String createdBy;
    private Long productCategoryId;
}
