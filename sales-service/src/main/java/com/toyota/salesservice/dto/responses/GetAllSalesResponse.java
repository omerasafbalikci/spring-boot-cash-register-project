package com.toyota.salesservice.dto.responses;

import com.toyota.salesservice.domain.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for sales used as response.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllSalesResponse {
    private Long id;
    private String salesNumber;
    private LocalDateTime salesDate;
    private String createdBy;
    private PaymentType paymentType;
    private Double totalPrice;
    private Double money;
    private Double change;
    private List<GetAllSalesItemsResponse> salesItemsList;
}
