package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.SalesRepository;
import com.toyota.salesservice.domain.Sales;
import com.toyota.salesservice.domain.SalesItems;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.responses.InventoryResponse;
import com.toyota.salesservice.service.abstracts.SalesService;
import com.toyota.salesservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                        .map(sale, SalesItems.class)).collect(Collectors.toList());
        sales.setSalesItemsList(salesItems);

        List<String> skuCodes = sales.getSalesItemsList().stream()
                .map(SalesItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://product-service/api/products/skucode",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponses)
                .allMatch(InventoryResponse::getIsInStock);

        if (allProductsInStock) {
            this.salesRepository.save(sales);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }
}
