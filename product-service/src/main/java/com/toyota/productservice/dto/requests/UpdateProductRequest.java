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
public class UpdateProductRequest {
    @NotNull
    private Long id;
    private String name;
    private String description;
    private int quantity;
    private Double unitPrice;
    private boolean state;
    private String imageUrl;
    private String createdBy;
    private LocalDateTime updatedAt;
    private Long productCategoryId;
}
