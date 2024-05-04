package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateReturnRequest;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesItemsResponse;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.dto.responses.PaginationResponse;

import java.time.LocalDateTime;

public interface SalesService {
    GetAllSalesResponse addSales(CreateSalesRequest createSalesRequest);
    GetAllSalesItemsResponse toReturn(CreateReturnRequest createReturnRequest);
    PaginationResponse<GetAllSalesResponse> getAllSalesPage(int page, int size, String[] sort, Long id, String salesNumber,
                                                                   LocalDateTime salesDate, String createdBy, String paymentType,
                                                                   Double totalPrice, Double money, Double change);
}
