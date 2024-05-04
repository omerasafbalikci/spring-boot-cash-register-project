package com.toyota.salesservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllSalesResponse {
    private Long id;
    private String salesNumber;
    private LocalDateTime salesDate;
    private String createdBy;
    private String paymentType;
    private Double totalPrice;
    private Double money;
    private Double change;
    private boolean deleted;
    private List<GetAllSalesItemsResponse> salesItemsList;
}
