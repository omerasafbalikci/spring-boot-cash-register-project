package com.toyota.salesservice.service.abstracts;

import com.toyota.salesservice.dto.requests.CreateSalesRequest;

public interface SalesService {
    void makeSales(CreateSalesRequest createSalesRequest);
}
