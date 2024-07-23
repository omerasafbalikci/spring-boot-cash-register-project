package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductCategoryControllerTest {
    @Mock
    private ProductCategoryService productCategoryService;
    @InjectMocks
    private ProductCategoryController productCategoryController;

    @Test
    void getProductCategoriesFiltered() {
        // Given
        Map<String, Object> response = new HashMap<>();
        when(productCategoryService.getCategoriesFiltered(0, 3, new String[]{"id", "asc"}, null, null, null, null)).thenReturn(response);

        // When
        ResponseEntity<Map<String, Object>> result = productCategoryController.getCategoriesFiltered(0, 3, new String[]{"id", "asc"}, null, null, null, null);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void getProductsByCategoryId() {
        // Given
        Map<String, Object> response = new HashMap<>();
        when(productCategoryService.getProductsByCategoryId(0, 3, new String[]{"id", "asc"}, 1L)).thenReturn(response);

        // When
        ResponseEntity<Map<String, Object>> result = productCategoryController.getProductsByCategoryId(0, 3, new String[]{"id", "asc"}, 1L);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void addCategory() {
        // Given
        CreateProductCategoryRequest request = new CreateProductCategoryRequest("New Category", "New Description", "New ImageURL", "Admin");
        GetAllProductCategoriesResponse response = new GetAllProductCategoriesResponse(1L, "Category Number", "New Category", "New Description", "New ImageURL", "Admin", LocalDateTime.now());

        // Mock
        when(productCategoryService.addCategory(any(CreateProductCategoryRequest.class))).thenReturn(response);

        // When
        ResponseEntity<GetAllProductCategoriesResponse> result = productCategoryController.addCategory(request);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response.getName(), result.getBody().getName());
        assertEquals(response.getDescription(), result.getBody().getDescription());
    }

    @Test
    void updateCategory() {
        // Given
        UpdateProductCategoryRequest request = new UpdateProductCategoryRequest(1L, "Updated Category", "Updated Description", "Updated ImageURL", "Admin");
        GetAllProductCategoriesResponse response = new GetAllProductCategoriesResponse(1L, "Updated Category Number", "Updated Category", "Updated Description", "Updated ImageURL", "Admin", LocalDateTime.now());

        // Mock
        when(productCategoryService.updateCategory(any(UpdateProductCategoryRequest.class))).thenReturn(response);

        // When
        ResponseEntity<GetAllProductCategoriesResponse> result = productCategoryController.updateCategory(request);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response.getName(), result.getBody().getName());
        assertEquals(response.getDescription(), result.getBody().getDescription());
    }

    @Test
    void deleteCategory() {
        // Given
        Long categoryId = 1L;
        GetAllProductCategoriesResponse response = new GetAllProductCategoriesResponse(categoryId, "Category Number", "Category Name", "Category Description", "ImageURL", "Admin", LocalDateTime.now());

        // Mock
        when(productCategoryService.deleteCategory(anyLong())).thenReturn(response);

        // When
        ResponseEntity<GetAllProductCategoriesResponse> result = productCategoryController.deleteCategory(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response.getId(), result.getBody().getId());
        assertEquals(response.getName(), result.getBody().getName());
        assertEquals(response.getDescription(), result.getBody().getDescription());
    }
}
