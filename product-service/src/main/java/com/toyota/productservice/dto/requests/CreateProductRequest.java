package com.toyota.productservice.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private int quantity;
    @NotNull
    private Double unitPrice;
    @NotNull
    private boolean state;
    private String imageUrl;
    @NotNull
    @NotBlank
    private String createdBy;
    private LocalDateTime updatedAt = LocalDateTime.now();
    @NotNull
    private Long productCategoryId;
}
