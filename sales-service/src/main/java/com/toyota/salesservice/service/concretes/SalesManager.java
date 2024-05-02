package com.toyota.salesservice.service.concretes;

import com.toyota.salesservice.dao.SalesRepository;
import com.toyota.salesservice.domain.Sales;
import com.toyota.salesservice.domain.SalesItems;
import com.toyota.salesservice.dto.requests.CreateSalesRequest;
import com.toyota.salesservice.dto.requests.InventoryRequest;
import com.toyota.salesservice.dto.responses.GetAllSalesResponse;
import com.toyota.salesservice.dto.responses.InventoryResponse;
import com.toyota.salesservice.service.abstracts.SalesService;
import com.toyota.salesservice.service.rules.SalesBusinessRules;
import com.toyota.salesservice.utilities.exceptions.FetchInventoryResponseException;
import com.toyota.salesservice.utilities.exceptions.InsufficientBalanceException;
import com.toyota.salesservice.utilities.exceptions.ProductStatusFalseException;
import com.toyota.salesservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private final SalesRepository salesRepository;
    private final Logger logger = LogManager.getLogger(SalesService.class);
    private final WebClient.Builder webClientBuilder;
    private final ModelMapperService modelMapperService;
    private final SalesBusinessRules salesBusinessRules;

    @Override
    public GetAllSalesResponse addSales(CreateSalesRequest createSalesRequest) {
        Sales sales = new Sales();
        sales.setSalesNumber(UUID.randomUUID().toString().substring(0, 8));
        sales.setSalesDate(LocalDateTime.now());
        sales.setCreatedBy(createSalesRequest.getCreatedBy());
        sales.setMoney(createSalesRequest.getMoney());

        List<InventoryRequest> inventoryRequests = createSalesRequest.getCreateSalesItemsRequests().stream()
                .map(salesItem -> this.modelMapperService.forRequest().map(salesItem, InventoryRequest.class))
                .toList();

        List<InventoryResponse> inventoryResponses = webClientBuilder.build().post()
                .uri("http://product-service/api/products/checkproductininventory")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {})
                .block();

        if (inventoryResponses != null && !inventoryResponses.isEmpty()) {
            List<SalesItems> salesItems = inventoryResponses.stream()
                    .map(response -> {
                        SalesItems salesItem = new SalesItems();
                        salesItem.setSales(sales);
                        salesItem.setBarcodeNumber(response.getBarcodeNumber());
                        salesItem.setSkuCode(response.getSkuCode());
                        salesItem.setName(response.getName());
                        salesItem.setQuantity(response.getQuantity());
                        salesItem.setUnitPrice(response.getUnitPrice());
                        salesItem.setState(response.getState());
                        return salesItem;
                    })
                    .toList();
            sales.setSalesItemsList(salesItems);

            for (SalesItems salesItem : salesItems) {
                if (!salesItem.getState()) {
                    throw new ProductStatusFalseException(salesItem.getName() + " status is false");
                }
            }

            double total = salesItems.stream()
                    .mapToDouble(salesItem -> salesItem.getUnitPrice() * salesItem.getQuantity())
                    .sum();

            if (sales.getMoney() >= total) {
                sales.setChange(sales.getMoney() - total);
                this.salesRepository.save(sales);
                return this.modelMapperService.forResponse().map(sales, GetAllSalesResponse.class);
            } else {
                webClientBuilder.build().post()
                        .uri("http://product-service/api/products/updateproductininventory")
                        .body(BodyInserters.fromValue(inventoryRequests))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {})
                        .block();
                throw new InsufficientBalanceException("Insufficient balance");
            }
        } else {
            throw new FetchInventoryResponseException("Error while fetching inventory response");
        }

    }
}
