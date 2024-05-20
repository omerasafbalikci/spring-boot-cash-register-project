package com.toyota.productservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for product_category used as response.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProductCategoriesResponse {
    private Long id;
    private String categoryNumber;
    private String name;
    private String description;
    private String imageUrl;
    private String createdBy;
    private LocalDateTime updatedAt;
}
