package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;

import java.util.TreeMap;

public interface SalesService {
    GetAllSalesResponse addSales(CreateSalesRequest createSalesRequest);
    public TreeMap<String, Object> getAllSalesPage(int page, int size, String[] sort);
    public GetAllSalesResponse getSalesBySalesNumber(String salesNumber);
    public GetAllSalesResponse getProductById(Long id);
}
