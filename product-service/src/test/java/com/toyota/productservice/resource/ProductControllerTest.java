package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.InventoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.dto.responses.InventoryResponse;
import com.toyota.productservice.service.abstracts.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductController productController;

    @Test
    void getProductFiltered() {
        // Given
        TreeMap<String, Object> response = new TreeMap<>();
        when(productService.getProductFiltered(0, 3, new String[]{"id", "asc"}, null, "", null)).thenReturn(response);

        // When
        ResponseEntity<TreeMap<String, Object>> result = productController.getProductFiltered(0, 3, new String[]{"id", "asc"}, null, "", null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getProductsByNameContaining() {
        // Given
        String name = "Product";
        TreeMap<String, Object> response = new TreeMap<>();
        when(productService.getProductsByNameContaining(name, 0, 3, new String[]{"id", "asc"})).thenReturn(response);

        // When
        ResponseEntity<TreeMap<String, Object>> result = productController.getProductsByNameContaining(name, 0, 3, new String[]{"id", "asc"});

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getProductsByInitialLetter() {
        // Given
        String initialLetter = "P";
        TreeMap<String, Object> response = new TreeMap<>();
        when(productService.getProductsByInitialLetter(initialLetter, 0, 3, new String[]{"id", "asc"})).thenReturn(response);

        // When
        ResponseEntity<TreeMap<String, Object>> result = productController.getProductsByInitialLetter(initialLetter, 0, 3, new String[]{"id", "asc"});

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void checkProductInInventory() {
        // Given
        List<InventoryRequest> inventoryRequests = new ArrayList<>();
        List<InventoryResponse> inventoryResponses = new ArrayList<>();
        when(productService.checkProductInInventory(inventoryRequests)).thenReturn(inventoryResponses);

        // When
        List<InventoryResponse> result = productController.checkProductInInventory(inventoryRequests);

        // Then
        assertEquals(inventoryResponses, result);
    }

    @Test
    void updateProductInInventory() {
        // Given
        List<InventoryRequest> inventoryRequests = new ArrayList<>();

        // When
        productController.updateProductInInventory(inventoryRequests);

        // Then
        verify(productService, times(1)).updateProductInInventory(inventoryRequests);
    }

    @Test
    void addProduct() {
        // Given
        CreateProductRequest request = new CreateProductRequest();
        GetAllProductsResponse response = new GetAllProductsResponse();
        when(productService.addProduct(request)).thenReturn(response);

        // When
        ResponseEntity<GetAllProductsResponse> result = productController.addProduct(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void updateProduct() {
        // Given
        UpdateProductRequest request = new UpdateProductRequest();
        GetAllProductsResponse response = new GetAllProductsResponse();
        when(productService.updateProduct(request)).thenReturn(response);

        // When
        ResponseEntity<GetAllProductsResponse> result = productController.updateProduct(request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void deleteProduct() {
        // Given
        Long id = 1L;
        GetAllProductsResponse response = new GetAllProductsResponse();
        when(productService.deleteProduct(id)).thenReturn(response);

        // When
        ResponseEntity<GetAllProductsResponse> result = productController.deleteProduct(id);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }
}
