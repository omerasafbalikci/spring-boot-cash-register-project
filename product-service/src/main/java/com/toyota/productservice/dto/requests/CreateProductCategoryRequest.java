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
public class CreateProductCategoryRequest {
    @NotNull
    @NotBlank
    private String name;
    private String imageUrl;
    @NotNull
    @NotBlank
    private String createdBy;
    private LocalDateTime updatedAt = LocalDateTime.now();
}
