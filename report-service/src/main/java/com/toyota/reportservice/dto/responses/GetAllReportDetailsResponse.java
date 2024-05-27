package com.toyota.reportservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllReportDetailsResponse {
    private Long id;
    private String barcodeNumber;
    private String name;
    private Integer quantity;
    private Double unitPrice;
    private Boolean state;
    private Double totalPrice;
    private String paymentType;
    private String campaignName;
}
