package com.toyota.salesservice.resource;

import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.service.abstracts.SalesService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@AllArgsConstructor
public class SalesController {
    @Autowired
    private final SalesService salesService;

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public String makeSales(@RequestBody CreateSalesRequest createSalesRequest) {
        this.salesService.makeSales(createSalesRequest);
        return "Make sale";
    }
}
