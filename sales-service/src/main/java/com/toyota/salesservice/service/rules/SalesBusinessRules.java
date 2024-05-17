package com.toyota.salesservice.service.rules;

import com.toyota.salesservice.dto.requests.InventoryRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class SalesBusinessRules {
    private final WebClient.Builder webClientBuilder;

    public void updateInventory(List<InventoryRequest> inventoryRequests) {
        Mono<Void> updateInventoryMono = this.webClientBuilder.build().post()
                .uri("http://product-service/api/products/update-product-in-inventory")
                .body(BodyInserters.fromValue(inventoryRequests))
                .retrieve()
                .toBodilessEntity()
                .then();

        updateInventoryMono.subscribe();
    }
}
