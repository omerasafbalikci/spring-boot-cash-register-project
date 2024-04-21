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
    @NotNull
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Integer quantity;
    @NotNull
    private Double unitPrice;
    @NotNull
    private Boolean state;
    private String imageUrl;
    @NotNull
    @NotBlank
    private String createdBy;
    @NotNull
    private Long productCategoryId;
}
