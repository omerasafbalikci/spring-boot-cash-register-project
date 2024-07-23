package com.toyota.productservice.service.concretes;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.dao.ProductCategorySpecification;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
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
import org.springframework.data.domain.*;

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
        productCategoryManager = new ProductCategoryManager(productCategoryRepository, modelMapperService, productCategoryBusinessRules);
    }

    @Test
    void getCategoriesFiltered_asc() {
        // Given
        int page = 0;
        int size = 3;
        String[] sort = {"id,asc"};

        ProductCategory productCategory1 = new ProductCategory(1L, "1234567890123", "ProductCategory1", "Description1", "imageUrl1", "Asaf", LocalDateTime.now(), null, false);
        ProductCategory productCategory2 = new ProductCategory(2L, "1234567890124", "ProductCategory2", "Description2", "imageUrl2", "Can", LocalDateTime.now(), null, false);
        List<ProductCategory> productCategoryList = Arrays.asList(productCategory1, productCategory2);

        Page<ProductCategory> productCategoryPage = new PageImpl<>(productCategoryList, PageRequest.of(page, size, Sort.by(Sort.Order.asc("id"))), productCategoryList.size());

        // When
        when(productCategoryRepository.findAll(any(ProductCategorySpecification.class), any(Pageable.class))).thenReturn(productCategoryPage);
        when(modelMapper.map(any(ProductCategory.class), eq(GetAllProductCategoriesResponse.class)))
                .thenAnswer(invocation -> {
                    ProductCategory productCategory = invocation.getArgument(0);
                    return new GetAllProductCategoriesResponse(productCategory.getId(), productCategory.getCategoryNumber(), productCategory.getName(), productCategory.getDescription(), productCategory.getImageUrl(), productCategory.getCreatedBy(), null);
                });
        when(modelMapperService.forResponse()).thenReturn(modelMapper);

        Map<String, Object> response = productCategoryManager.getCategoriesFiltered(page, size, sort, null, null, null, null);

        // Then
        @SuppressWarnings("unchecked")
        List<GetAllProductCategoriesResponse> productCategories = (List<GetAllProductCategoriesResponse>) response.get("productCategories");

        assertEquals(2, productCategories.size());
        assertEquals(0, response.get("currentPage"));
        assertEquals(2L, response.get("totalItems"));
        assertEquals(1, response.get("totalPages"));
    }

    @Test
    void addCategory_shouldAddNewCategory_whenCategoryDoesNotExist() {
        // Given
        CreateProductCategoryRequest request = new CreateProductCategoryRequest("TestCategory", "Description", "ImageURL", "Admin");
        ProductCategory productCategory = new ProductCategory(null, null, "TestCategory", "Description", "ImageURL", "Admin", null, null, false);
        GetAllProductCategoriesResponse response = new GetAllProductCategoriesResponse(1L, "CategoryNumber", "TestCategory", "Description", "ImageURL", "Admin", LocalDateTime.now());

        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(modelMapperService.forRequest()).thenReturn(modelMapper);
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
        ProductCategory existingProductCategory = new ProductCategory(categoryId, "Category Number", "Existing Category", "Existing Description", "Existing ImageURL", "Admin", LocalDateTime.now(), null, false);

        // Repository mock
        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(productCategoryRepository.save(any(ProductCategory.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(productCategoryRepository.findByIdAndDeletedFalse(categoryId)).thenReturn(Optional.of(existingProductCategory));
        when(productCategoryRepository.existsByNameIgnoreCaseAndDeletedIsFalse(categoryName)).thenReturn(false);

        // ModelMapper mock
        GetAllProductCategoriesResponse getAllProductCategoriesResponse = new GetAllProductCategoriesResponse(1L, "Category Number", categoryName, "Updated Description", "Updated ImageURL", "Admin", null);
        when(modelMapper.map(any(ProductCategory.class), eq(GetAllProductCategoriesResponse.class))).thenReturn(getAllProductCategoriesResponse);

        // When
        GetAllProductCategoriesResponse response = productCategoryManager.updateCategory(request);

        // Then
        Mockito.verify(productCategoryRepository).save(any(ProductCategory.class));
        Mockito.verify(productCategoryBusinessRules).checkUpdate(request, existingProductCategory);
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

        when(productCategoryRepository.findByIdAndDeletedFalse(categoryId)).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> productCategoryManager.updateCategory(request));

        assertEquals("Product category not found", exception.getMessage());
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

        when(productCategoryRepository.findByIdAndDeletedFalse(categoryId)).thenReturn(Optional.of(existingProductCategory));
        when(productCategoryRepository.existsByNameIgnoreCaseAndDeletedIsFalse(categoryName)).thenReturn(true);

        // When / Then
        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () -> productCategoryManager.updateCategory(request));

        assertEquals("Product category name already exists", exception.getMessage());
    }

    @Test
    void deleteCategory_success() {
        // Given
        Long categoryId = 1L;
        ProductCategory existingProductCategory = new ProductCategory(categoryId, "Category Number", "Existing Category", "Existing Description", "Existing ImageURL", "Admin", LocalDateTime.now(), null, false);

        GetAllProductCategoriesResponse productCategoriesResponse = new GetAllProductCategoriesResponse(categoryId, "Category Number", "Existing Category", "Existing Description", "Existing ImageURL", "Admin", LocalDateTime.now());

        when(modelMapperService.forResponse()).thenReturn(modelMapper);
        when(productCategoryRepository.findByIdAndDeletedFalse(categoryId)).thenReturn(Optional.of(existingProductCategory));
        when(modelMapper.map(existingProductCategory, GetAllProductCategoriesResponse.class)).thenReturn(productCategoriesResponse);

        // When
        GetAllProductCategoriesResponse response = productCategoryManager.deleteCategory(categoryId);

        // Then
        verify(productCategoryRepository).findByIdAndDeletedFalse(categoryId);
        verify(productCategoryRepository).save(existingProductCategory);
        assertNotNull(response);
        assertEquals(existingProductCategory.getName(), response.getName());
    }

    @Test
    void deleteCategory_categoryNotFound() {
        // Given
        Long categoryId = 1L;

        // When
        when(productCategoryRepository.findByIdAndDeletedFalse(categoryId)).thenReturn(Optional.empty());

        // Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> productCategoryManager.deleteCategory(categoryId));
        assertEquals("Product category not found", exception.getMessage());
    }
}
