package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;

public interface SalesService {
    GetAllSalesResponse addSales(CreateSalesRequest createSalesRequest);
}
