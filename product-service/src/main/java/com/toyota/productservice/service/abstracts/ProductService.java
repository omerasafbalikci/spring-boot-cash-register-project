package com.toyota.productservice.service.abstracts;

import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.InventoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.dto.responses.InventoryResponse;

import java.util.List;
import java.util.TreeMap;

public interface ProductService {
    TreeMap<String, Object> getProductFiltered(int page, int size, String[] sort, Long id, String barcodeNumber, Boolean state);
    TreeMap<String, Object> getProductsByNameContaining(String name, int page, int size, String[] sort);
    TreeMap<String, Object> getProductsByInitialLetter(String initialLetter, int page, int size, String[] sort);
    List<InventoryResponse> checkProductInInventory(List<InventoryRequest> inventoryRequests);
    void updateProductInInventory(List<InventoryRequest> inventoryRequests);
    void returnedProduct(InventoryRequest inventoryRequest);
    GetAllProductsResponse addProduct(CreateProductRequest createProductRequest);
    GetAllProductsResponse updateProduct(UpdateProductRequest updateProductRequest);
    GetAllProductsResponse deleteProduct(Long id);
}
