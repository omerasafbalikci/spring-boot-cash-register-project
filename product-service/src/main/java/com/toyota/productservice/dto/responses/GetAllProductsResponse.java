package com.toyota.productservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAllProductsResponse {
    private Long id;
    private String name;
    private String description;
    private int quantity;
    private Double unitPrice;
    private boolean state;
    private String imageUrl;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String productCategoryName;
}
