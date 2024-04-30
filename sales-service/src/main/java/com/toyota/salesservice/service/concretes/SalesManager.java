package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.SalesRepository;
import com.toyota.salesservice.domain.Campaign;
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
        sales.setMoney(createSalesRequest.getMoney());

        List<InventoryRequest> inventoryRequests = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(salesItem -> this.modelMapperService.forRequest()
                        .map(salesItem, InventoryRequest.class)).toList();

        List<InventoryResponse> inventoryResponses = webClientBuilder.build().post()
                .uri("http://product-service/api/products/checkproductininventory")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {})
                .block();

        boolean allProductsInStock;
        if (inventoryResponses != null) {
            allProductsInStock = inventoryResponses.stream()
                    .allMatch(InventoryResponse::getIsInStock);
        } else {
            throw new RuntimeException();
        }

        List<SalesItems> salesItems = inventoryResponses.stream()
                .map(request -> {
                    SalesItems salesItem = new SalesItems();
                    salesItem.setBarcodeNumber(request.getBarcodeNumber());
                    salesItem.setSkuCode(request.getSkuCode());
                    salesItem.setName(request.getName());
                    salesItem.setQuantity(request.getQuantity());
                    salesItem.setUnitPrice(request.getUnitPrice());
                    salesItem.setState(request.getState());

                    return salesItem;
                })
                .toList();
        sales.setSalesItemsList(salesItems);

        if (allProductsInStock) {
            this.salesRepository.save(sales);
        } else {
            throw new ProductIsNotInStock("Product is not in stock");
        }
    }
}
