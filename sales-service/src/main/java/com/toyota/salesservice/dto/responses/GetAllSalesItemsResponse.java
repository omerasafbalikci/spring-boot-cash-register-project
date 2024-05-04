package com.toyota.salesservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllSalesItemsResponse {
    private Long id;
    private String barcodeNumber;
    private String name;
    private Integer quantity;
    private Double unitPrice;
    private Boolean state;
    private Long campaignName;
}
