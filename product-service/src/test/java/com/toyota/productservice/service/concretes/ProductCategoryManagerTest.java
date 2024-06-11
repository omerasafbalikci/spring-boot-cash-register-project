package com.toyota.productservice.service.concretes;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.service.rules.ProductCategoryBusinessRules;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import com.toyota.productservice.utilities.exceptions.EntityNotFoundException;
import com.toyota.productservice.utilities.mappers.ModelMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductCategoryManagerTest {
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ModelMapperService modelMapperService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ProductCategoryBusinessRules productCategoryBusinessRules;
    private ProductCategoryManager productCategoryManager;

    @BeforeEach
    void setUp() {
        modelMapperService = mock(ModelMapperService.class);
        lenient().when(modelMapperService.forResponse()).thenReturn(modelMapper);
        lenient().when(modelMapperService.forRequest()).thenReturn(modelMapper);
        productCategoryManager = new ProductCategoryManager(productCategoryRepository, modelMapperService, productCategoryBusinessRules);
    }

    @Test
    void getAllCategories_shouldReturnAllCategories() {
        // Given
        List<ProductCategory> productCategories = new ArrayList<>();
        productCategories.add(new ProductCategory(1L, "CategoryNumber1", "Category 1", "Description 1", "ImageURL1", "Admin", LocalDateTime.now(), null));
        productCategories.add(new ProductCategory(2L, "CategoryNumber2", "Category 2", "Description 2", "ImageURL2", "Admin", LocalDateTime.now(), null));

        when(productCategoryRepository.findAll()).thenReturn(productCategories);

        GetAllProductCategoriesResponse response1 = new GetAllProductCategoriesResponse(1L, "CategoryNumber1", "Category 1", "Description 1", "ImageURL1", "Admin", LocalDateTime.now());
        GetAllProductCategoriesResponse response2 = new GetAllProductCategoriesResponse(2L, "CategoryNumber2", "Category 2", "Description 2", "ImageURL2", "Admin", LocalDateTime.now());

        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapper.map(any(ProductCategory.class), eq(GetAllProductCategoriesResponse.class))).thenReturn(response1, response2);

        // When
        List<GetAllProductCategoriesResponse> result = productCategoryManager.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("CategoryNumber1", result.get(0).getCategoryNumber());
        assertEquals("Category 1", result.get(0).getName());
        assertEquals("Description 1", result.get(0).getDescription());
        assertEquals("ImageURL1", result.get(0).getImageUrl());
        assertEquals("Admin", result.get(0).getCreatedBy());
        assertNotNull(result.get(0).getUpdatedAt());

        assertEquals(2L, result.get(1).getId());
        assertEquals("CategoryNumber2", result.get(1).getCategoryNumber());
        assertEquals("Category 2", result.get(1).getName());
        assertEquals("Description 2", result.get(1).getDescription());
        assertEquals("ImageURL2", result.get(1).getImageUrl());
        assertEquals("Admin", result.get(1).getCreatedBy());
        assertNotNull(result.get(1).getUpdatedAt());
    }

    @Test
    void getCategoriesByNameContaining_shouldReturnMatchingCategories_whenCategoriesExist() {
        // Given
        String name = "test";
        List<ProductCategory> productCategories = new ArrayList<>();
        productCategories.add(new ProductCategory(1L, "CategoryNumber1", "Test Category 1", "Description 1", "ImageURL1", "Admin", null, null));
        productCategories.add(new ProductCategory(2L, "CategoryNumber2", "Test Category 2", "Description 2", "ImageURL2", "Admin", null, null));

        when(productCategoryRepository.findByNameContainingIgnoreCase(name)).thenReturn(productCategories);

        GetAllProductCategoriesResponse response1 = new GetAllProductCategoriesResponse(1L, "CategoryNumber1", "Test Category 1", "Description 1", "ImageURL1", "Admin", null);
        GetAllProductCategoriesResponse response2 = new GetAllProductCategoriesResponse(2L, "CategoryNumber2", "Test Category 2", "Description 2", "ImageURL2", "Admin", null);

        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapper.map(any(ProductCategory.class), eq(GetAllProductCategoriesResponse.class))).thenReturn(response1, response2);

        // When
        List<GetAllProductCategoriesResponse> result = productCategoryManager.getCategoriesByNameContaining(name);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Category 1", result.get(0).getName());
        assertEquals("Test Category 2", result.get(1).getName());
    }

    @Test
    void getCategoriesByNameContaining_shouldThrowException_whenNoCategoriesFound() {
        // Given
        String name = "nonexistent";
        when(productCategoryRepository.findByNameContainingIgnoreCase(name)).thenReturn(new ArrayList<>());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> productCategoryManager.getCategoriesByNameContaining(name));
    }

    @Test
    void getCategoryById_shouldReturnCategory_whenCategoryExists() {
        // Given
        Long id = 1L;
        ProductCategory productCategory = new ProductCategory(id, "CategoryNumber1", "Test Category 1", "Description 1", "ImageURL1", "Admin", null, null);

        when(productCategoryRepository.findById(id)).thenReturn(java.util.Optional.of(productCategory));

        GetAllProductCategoriesResponse response = new GetAllProductCategoriesResponse(id, "CategoryNumber1", "Test Category 1", "Description 1", "ImageURL1", "Admin", null);

        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapper.map(productCategory, GetAllProductCategoriesResponse.class)).thenReturn(response);

        // When
        GetAllProductCategoriesResponse result = productCategoryManager.getCategoryById(id);

        // Then
        assertNotNull(result);
        assertEquals("Test Category 1", result.getName());
    }

    @Test
    void getCategoryById_shouldThrowException_whenCategoryDoesNotExist() {
        // Given
        Long id = 1L;
        when(productCategoryRepository.findById(id)).thenReturn(java.util.Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> productCategoryManager.getCategoryById(id));
    }

    @Test
    void getProductsByCategoryId_shouldReturnProducts_whenCategoryExists() {
        // Given
        Long categoryId = 1L;
        ProductCategory productCategory = new ProductCategory(categoryId, "CategoryNumber1", "Test Category 1", "Description 1", "ImageURL1", "Admin", null, null);
        Product product1 = new Product(1L, "BarcodeNumber1", "Test Product 1", "Description 1", 10, 100.0, true, "ImageURL1", "Admin", null, productCategory);
        Product product2 = new Product(2L, "BarcodeNumber2", "Test Product 2", "Description 2", 20, 200.0, true, "ImageURL2", "Admin", null, productCategory);
        productCategory.setProducts(Arrays.asList(product1, product2));

        when(productCategoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(productCategory));

        GetAllProductsResponse response1 = new GetAllProductsResponse(1L, "BarcodeNumber1", "Test Product 1", "Description 1", 10, 100.0, true, "ImageURL1", "Admin", null, "Test Category 1");
        GetAllProductsResponse response2 = new GetAllProductsResponse(2L, "BarcodeNumber2", "Test Product 2", "Description 2", 20, 200.0, true, "ImageURL2", "Admin", null, "Test Category 1");

        when(modelMapper.map(product1, GetAllProductsResponse.class)).thenReturn(response1);
        when(modelMapper.map(product2, GetAllProductsResponse.class)).thenReturn(response2);

        // When
        List<GetAllProductsResponse> result = productCategoryManager.getProductsByCategoryId(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Product 1", result.get(0).getName());
        assertEquals("Test Product 2", result.get(1).getName());
        assertEquals("Test Category 1", result.get(0).getProductCategoryName());
        assertEquals("Test Category 1", result.get(1).getProductCategoryName());
    }

    @Test
    void getProductsByCategoryId_shouldThrowException_whenCategoryDoesNotExist() {
        // Given
        Long categoryId = 1L;
        when(productCategoryRepository.findById(categoryId)).thenReturn(java.util.Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> productCategoryManager.getProductsByCategoryId(categoryId));
    }

    @Test
    void addCategory_shouldAddNewCategory_whenCategoryDoesNotExist() {
        // Given
        CreateProductCategoryRequest request = new CreateProductCategoryRequest("TestCategory", "Description", "ImageURL", "Admin");
        ProductCategory productCategory = new ProductCategory(null, null, "TestCategory", "Description", "ImageURL", "Admin", null, null);
        GetAllProductCategoriesResponse response = new GetAllProductCategoriesResponse(1L, "CategoryNumber", "TestCategory", "Description", "ImageURL", "Admin", LocalDateTime.now());

        when(modelMapperService.forRequest().map(request, ProductCategory.class)).thenReturn(productCategory);
        when(productCategoryRepository.save(productCategory)).thenReturn(productCategory);
        when(modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class)).thenReturn(response);
        doNothing().when(productCategoryBusinessRules).checkIfProductCategoryNameExists(request.getName());

        // When
        GetAllProductCategoriesResponse result = productCategoryManager.addCategory(request);

        // Then
        assertNotNull(result);
        assertEquals("TestCategory", result.getName());
        assertEquals("Description", result.getDescription());
        assertEquals("ImageURL", result.getImageUrl());
        assertEquals("Admin", result.getCreatedBy());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void addCategory_shouldThrowException_whenCategoryExists() {
        // Given
        CreateProductCategoryRequest request = new CreateProductCategoryRequest("TestCategory", "Description", "ImageURL", "Admin");

        Mockito.doThrow(EntityAlreadyExistsException.class).when(productCategoryBusinessRules).checkIfProductCategoryNameExists(request.getName());

        // When / Then
        assertThrows(EntityAlreadyExistsException.class, () -> productCategoryManager.addCategory(request));
    }

    @Test
    void updateCategory_shouldUpdateCategory_whenCategoryExistsAndNameIsUnique() {
        // Given
        Long categoryId = 1L;
        String categoryName = "Updated Category";
        UpdateProductCategoryRequest request = new UpdateProductCategoryRequest(categoryId, categoryName, "Updated Description", "Updated ImageURL", "Admin");
        ProductCategory existingProductCategory = new ProductCategory(categoryId, "Category Number", "Existing Category", "Existing Description", "Existing ImageURL", "Admin", LocalDateTime.now(), null);

        // Repository mock
        when(productCategoryRepository.save(any(ProductCategory.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(productCategoryRepository.findById(any())).thenReturn(Optional.of(existingProductCategory));

        // ModelMapper mock
        GetAllProductCategoriesResponse getAllProductCategoriesResponse = new GetAllProductCategoriesResponse(1L, "Category Number", categoryName, "Updated Description", "Updated ImageURL", "Admin", null);
        when(modelMapper.map(any(ProductCategory.class), eq(GetAllProductCategoriesResponse.class))).thenReturn(getAllProductCategoriesResponse);

        GetAllProductCategoriesResponse response = productCategoryManager.updateCategory(request);

        // Then
        Mockito.verify(productCategoryRepository).save(any(ProductCategory.class));
        assertNotNull(response);
        assertEquals(request.getId(), response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getImageUrl(), response.getImageUrl());
        assertEquals(request.getCreatedBy(), response.getCreatedBy());
    }

    @Test
    void updateCategory_shouldThrowException_whenCategoryDoesNotExist() {
        // Given
        Long categoryId = 1L;
        UpdateProductCategoryRequest request = new UpdateProductCategoryRequest(categoryId, "Updated Category", "Updated Description", "Updated ImageURL", "Admin");

        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> productCategoryManager.updateCategory(request));
    }

    @Test
    void updateCategory_shouldThrowException_whenCategoryNameAlreadyExists() {
        // Given
        Long categoryId = 1L;
        String categoryName = "Updated Category";
        UpdateProductCategoryRequest request = new UpdateProductCategoryRequest(categoryId, categoryName, "Updated Description", "Updated ImageURL", "Admin");

        ProductCategory existingProductCategory = new ProductCategory();
        existingProductCategory.setId(categoryId);
        existingProductCategory.setName("Existing Category");

        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(existingProductCategory));
        when(productCategoryRepository.existsByNameIgnoreCase(categoryName)).thenReturn(true);

        // When / Then
        assertThrows(EntityAlreadyExistsException.class, () -> productCategoryManager.updateCategory(request));
    }

    @Test
    void deleteCategory_success() {
        // Given
        Long categoryId = 1L;
        ProductCategory existingProductCategory = new ProductCategory(categoryId, "Category Number", "Existing Category", "Existing Description", "Existing ImageURL", "Admin", LocalDateTime.now(), null);

        GetAllProductCategoriesResponse productCategoriesResponse = new GetAllProductCategoriesResponse(categoryId, "Category Number", "Existing Category", "Existing Description", "Existing ImageURL", "Admin", LocalDateTime.now());

        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(existingProductCategory));
        when(modelMapper.map(existingProductCategory, GetAllProductCategoriesResponse.class)).thenReturn(productCategoriesResponse);

        // When
        GetAllProductCategoriesResponse response = productCategoryManager.deleteCategory(categoryId);

        // Then
        verify(productCategoryRepository).findById(categoryId);
        verify(productCategoryRepository).deleteById(categoryId);
        assertNotNull(response);
        assertEquals(existingProductCategory.getName(), response.getName());
    }

    @Test
    void deleteUser_userNotFound() {
        // Given
        Long userId = 1L;

        // When
        // Repository mock
        when(productCategoryRepository.findById(any())).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> productCategoryManager.deleteCategory(userId));
    }
}
