package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
    void getAllCategories() {
        // Given
        GetAllProductCategoriesResponse response1 = new GetAllProductCategoriesResponse(1L, "CategoryNumber1", "Category 1", "Description 1", "ImageURL1", "Admin", LocalDateTime.now());
        GetAllProductCategoriesResponse response2 = new GetAllProductCategoriesResponse(2L, "CategoryNumber2", "Category 2", "Description 2", "ImageURL2", "Admin", LocalDateTime.now());
        List<GetAllProductCategoriesResponse> responses = Arrays.asList(response1, response2);

        // Mock
        when(productCategoryService.getAllCategories()).thenReturn(responses);

        // When
        ResponseEntity<List<GetAllProductCategoriesResponse>> result = productCategoryController.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        assertEquals(response1.getName(), result.getBody().get(0).getName());
        assertEquals(response2.getName(), result.getBody().get(1).getName());
    }

    @Test
    void getCategoriesByNameContaining() {
        // Given
        String name = "Category";
        GetAllProductCategoriesResponse response1 = new GetAllProductCategoriesResponse(1L, "CategoryNumber1", "Category 1", "Description 1", "ImageURL1", "Admin", LocalDateTime.now());
        GetAllProductCategoriesResponse response2 = new GetAllProductCategoriesResponse(2L, "CategoryNumber2", "Category 2", "Description 2", "ImageURL2", "Admin", LocalDateTime.now());
        List<GetAllProductCategoriesResponse> responses = Arrays.asList(response1, response2);

        // Mock
        when(productCategoryService.getCategoriesByNameContaining(name)).thenReturn(responses);

        // When
        ResponseEntity<List<GetAllProductCategoriesResponse>> result = productCategoryController.getCategoriesByNameContaining(name);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        assertEquals(response1.getName(), result.getBody().get(0).getName());
        assertEquals(response2.getName(), result.getBody().get(1).getName());
    }

    @Test
    void getCategoryById() {
        // Given
        Long id = 1L;
        GetAllProductCategoriesResponse response = new GetAllProductCategoriesResponse(id, "CategoryNumber1", "Category 1", "Description 1", "ImageURL1", "Admin", LocalDateTime.now());

        // Mock
        when(productCategoryService.getCategoryById(id)).thenReturn(response);

        // When
        ResponseEntity<GetAllProductCategoriesResponse> result = productCategoryController.getCategoryById(id);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response.getName(), result.getBody().getName());
        assertEquals(response.getDescription(), result.getBody().getDescription());
    }

    @Test
    void getProductsByCategoryId() {
        // Given
        Long categoryId = 1L;
        List<GetAllProductsResponse> responses = Arrays.asList(
                new GetAllProductsResponse(1L, "BarcodeNumber1", "Test Product 1", "Description 1", 10, 100.0, true, "ImageURL1", "Admin", null, "productCategory"),
                new GetAllProductsResponse(2L, "BarcodeNumber2", "Test Product 2", "Description 2", 20, 200.0, true, "ImageURL2", "Admin", null, "productCategory")
        );

        // Mock
        when(productCategoryService.getProductsByCategoryId(categoryId)).thenReturn(responses);

        // When
        ResponseEntity<List<GetAllProductsResponse>> result = productCategoryController.getProductsByCategoryId(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        assertEquals(responses.get(0).getName(), result.getBody().get(0).getName());
        assertEquals(responses.get(1).getName(), result.getBody().get(1).getName());
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
