package com.toyota.productservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for inventory response.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    private String name;
    private Integer quantity;
    private Boolean isInStock;
    private Double unitPrice;
    private Boolean state;
}
