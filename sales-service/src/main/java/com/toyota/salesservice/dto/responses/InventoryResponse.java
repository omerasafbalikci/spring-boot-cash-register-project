package com.toyota.salesservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for inventory response.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryResponse {
    private String name;
    private Integer quantity;
    private Boolean isInStock;
    private Double unitPrice;
    private Boolean state;
}