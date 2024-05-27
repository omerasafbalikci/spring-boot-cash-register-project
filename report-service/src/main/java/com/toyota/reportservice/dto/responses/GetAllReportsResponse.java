package com.toyota.reportservice.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllReportsResponse {
    private Long id;
    private String salesNumber;
    private LocalDateTime salesDate;
    private String createdBy;
    private String paymentType;
    private Double totalPrice;
    private Double money;
    private Double change;
    private List<GetAllReportDetailsResponse> salesItemsList;
}
