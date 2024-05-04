package com.toyota.salesservice.resource;

import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.service.abstracts.SalesService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
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

    @GetMapping()
    public List<GetAllSalesResponse> getAllSalesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(defaultValue = "") Long id,
            @RequestParam(defaultValue = "") String salesNumber,
            @RequestParam(defaultValue = "") LocalDateTime salesDate,
            @RequestParam(defaultValue = "") String createdBy,
            @RequestParam(defaultValue = "") String paymentType,
            @RequestParam(defaultValue = "") Double totalPrice,
            @RequestParam(defaultValue = "") Double money,
            @RequestParam(defaultValue = "") Double change
    ) {
        return this.salesService.getAllSalesPage(page, size, sort, id, salesNumber, salesDate, createdBy, paymentType, totalPrice, money, change);
    }
}
