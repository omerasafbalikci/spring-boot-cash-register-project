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
    private String barcodeNumber;
    private String skuCode;
    private String name;
    private String description;
    private Integer quantity;
    private Double unitPrice;
    private Boolean state;
    private String imageUrl;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String productCategoryName;
}
