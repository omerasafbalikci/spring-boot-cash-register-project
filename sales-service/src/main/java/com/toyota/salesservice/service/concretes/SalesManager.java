package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.SalesRepository;
import com.toyota.salesservice.domain.Sales;
import com.toyota.salesservice.domain.SalesItems;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.requests.InventoryRequest;
import com.toyota.salesservice.dto.responses.InventoryResponse;
import com.toyota.salesservice.service.abstracts.SalesService;
import com.toyota.salesservice.utilities.exceptions.ProductIsNotInStock;
import com.toyota.salesservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class SalesManager implements SalesService {
    @Autowired
    private final SalesRepository salesRepository;
    @Autowired
    private final WebClient.Builder webClientBuilder;
    @Autowired
    private final ModelMapperService modelMapperService;

    public void makeSales(CreateSalesRequest createSalesRequest) {
        Sales sales = new Sales();
        sales.setSalesNumber(UUID.randomUUID().toString().substring(0, 8));
        sales.setSalesDate(LocalDateTime.now());

        List<SalesItems> salesItems = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(sale -> this.modelMapperService.forRequest()
                        .map(sale, SalesItems.class)).toList();
        sales.setSalesItemsList(salesItems);

        List<InventoryRequest> inventoryRequests = salesItems.stream()
                .map(salesItem -> this.modelMapperService.forRequest()
                        .map(salesItem, InventoryRequest.class)).toList();

        List<InventoryResponse> inventoryResponses = webClientBuilder.build().post()
                .uri("http://product-service/api/products/checkproduct")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {})
                .block();

        boolean allProductsInStock = inventoryResponses.stream()
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            this.salesRepository.save(sales);
        } else {
            throw new ProductIsNotInStock("Product is not in stock");
        }
    }
}
