package com.toyota.salesservice.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSalesRequest {
    private Double money;
    private Double change;
    private List<CreateSalesItemsRequest> createSalesItemsRequests;
}
