package com.toyota.productservice.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductRequest {
    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    private String name;
    private String description;
    @NotNull(message = "Quantity must not be null")
    private Integer quantity;
    @NotNull(message = "Unit price must not be null")
    private Double unitPrice;
    @NotNull(message = "State must not be null")
    private Boolean state;
    private String imageUrl;
    @NotNull(message = "Created by must not be null")
    @NotBlank(message = "Created by must not be blank")
    private String createdBy;
    @NotNull(message = "Product category id must not be null")
    private Long productCategoryId;
}
