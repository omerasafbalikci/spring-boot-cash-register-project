package com.toyota.salesservice.service.rules;

import com.toyota.salesservice.dto.requests.InventoryRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@AllArgsConstructor
public class SalesBusinessRules {
    private final WebClient.Builder webClientBuilder;

    public void updateInventory(List<InventoryRequest> inventoryRequests) {
        this.webClientBuilder.build().put()
                .uri("http://product-service/api/products/update-product-in-inventory")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
