package com.toyota.productservice.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to update product category.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCategoryRequest {
    @NotNull(message = "Id must not be null")
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    @NotNull(message = "Created by must not be null")
    @NotBlank(message = "Created by must not be blank")
    private String createdBy;
}
