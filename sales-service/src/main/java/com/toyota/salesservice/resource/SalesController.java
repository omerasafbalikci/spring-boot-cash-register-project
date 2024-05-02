package com.toyota.salesservice.resource;

import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.service.abstracts.SalesService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/sales")
@AllArgsConstructor
public class SalesController {
    private final SalesService salesService;

    @PostMapping("/add")
    public GetAllSalesResponse addSales(@RequestBody @Valid CreateSalesRequest createSalesRequest) {
        return this.salesService.addSales(createSalesRequest);
    }
}
