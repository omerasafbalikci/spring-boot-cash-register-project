package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

public interface SalesService {
    GetAllSalesResponse addSales(CreateSalesRequest createSalesRequest);
    public List<GetAllSalesResponse> getAllSalesPage(int page, int size, String[] sort, Long id, String salesNumber,
                                                     LocalDateTime salesDate, String createdBy, String paymentType,
                                                     Double totalPrice, Double money, Double change);
}
