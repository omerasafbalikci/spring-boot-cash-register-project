package com.toyota.productservice.service.concretes;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.dao.ProductRepository;
import com.toyota.productservice.dao.ProductSpecification;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.InventoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.dto.responses.InventoryResponse;
import com.toyota.productservice.service.rules.ProductBusinessRules;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import com.toyota.productservice.utilities.exceptions.EntityNotFoundException;
import com.toyota.productservice.utilities.exceptions.ProductIsNotInStockException;
import com.toyota.productservice.utilities.mappers.ModelMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ProductManagerTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ModelMapperService modelMapperService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ProductBusinessRules productBusinessRules;
    private ProductManager productManager;

    @BeforeEach
    void setUp() {
        modelMapperService = mock(ModelMapperService.class);
        lenient().when(modelMapperService.forResponse()).thenReturn(modelMapper);
        lenient().when(modelMapperService.forRequest()).thenReturn(modelMapper);
        productManager = new ProductManager(productRepository, productCategoryRepository, modelMapperService, productBusinessRules);
    }

    @Test
    void getProductFiltered_asc() {
        // Given
        int page = 0;
        int size = 3;
        String[] sort = {"id,asc"};

        Product product1 = new Product(1L, "1234567890123", "Product1", "Description1", 10, 100.0, true, "imageUrl1", "Asaf", LocalDateTime.now(), null);
        Product product2 = new Product(2L, "1234567890124", "Product2", "Description2", 20, 200.0, true, "imageUrl2", "Can", LocalDateTime.now(), null);
        List<Product> productList = Arrays.asList(product1, product2);

        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(page, size, Sort.by(Sort.Order.asc("id"))), productList.size());

        // When
        when(productRepository.findAll(any(ProductSpecification.class), any(Pageable.class))).thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(GetAllProductsResponse.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    return new GetAllProductsResponse(product.getId(), product.getBarcodeNumber(), product.getName(), product.getDescription(), product.getQuantity(), product.getUnitPrice(), product.getState(), product.getImageUrl(), product.getCreatedBy(), product.getUpdatedAt(), null);
                });

        TreeMap<String, Object> response = productManager.getProductFiltered(page, size, sort, null, null, null);

        // Then
        @SuppressWarnings("unchecked")
        List<GetAllProductsResponse> products = (List<GetAllProductsResponse>) response.get("products");

        assertEquals(2, products.size());
        assertEquals(0, response.get("currentPage"));
        assertEquals(2L, response.get("totalItems"));
        assertEquals(1, response.get("totalPages"));
    }

    @Test
    void getProductFiltered_desc() {
        // Given
        int page = 0;
        int size = 3;
        String[] sort = {"id,desc"};

        Product product1 = new Product(1L, "1234567890123", "Product1", "Description1", 10, 100.0, true, "imageUrl1", "Asaf", LocalDateTime.now(), null);
        Product product2 = new Product(2L, "1234567890124", "Product2", "Description2", 20, 200.0, true, "imageUrl2", "Can", LocalDateTime.now(), null);
        List<Product> productList = Arrays.asList(product1, product2);

        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(page, size, Sort.by(Sort.Order.asc("id"))), productList.size());

        // When
        when(productRepository.findAll(any(ProductSpecification.class), any(Pageable.class))).thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(GetAllProductsResponse.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    return new GetAllProductsResponse(product.getId(), product.getBarcodeNumber(), product.getName(), product.getDescription(), product.getQuantity(), product.getUnitPrice(), product.getState(), product.getImageUrl(), product.getCreatedBy(), product.getUpdatedAt(), null);
                });

        TreeMap<String, Object> response = productManager.getProductFiltered(page, size, sort, null, null, null);

        // Then
        @SuppressWarnings("unchecked")
        List<GetAllProductsResponse> products = (List<GetAllProductsResponse>) response.get("products");

        assertEquals(2, products.size());
        assertEquals(0, response.get("currentPage"));
        assertEquals(2L, response.get("totalItems"));
        assertEquals(1, response.get("totalPages"));
    }

    @Test
    void getProductsByNameContaining() {
        // Given
        String name = "Product";
        int page = 0;
        int size = 3;
        String[] sort = {"id,asc"};

        Product product1 = new Product(1L, "1234567890123", "Product1", "Description1", 10, 100.0, true, "imageUrl1", "Asaf", LocalDateTime.now(), null);
        Product product2 = new Product(2L, "1234567890124", "Product2", "Description2", 20, 200.0, true, "imageUrl2", "Can", LocalDateTime.now(), null);
        List<Product> productList = Arrays.asList(product1, product2);

        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(page, size, Sort.by(Sort.Order.asc("id"))), productList.size());

        when(productRepository.findByNameContainingIgnoreCase(eq(name), any(Pageable.class))).thenReturn(productPage);
        when(modelMapper.map(any(Product.class), eq(GetAllProductsResponse.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    if (product == null) {
                        return null;
                    }
                    return new GetAllProductsResponse(product.getId(), product.getBarcodeNumber(), product.getName(), product.getDescription(), product.getQuantity(), product.getUnitPrice(), product.getState(), product.getImageUrl(), product.getCreatedBy(), product.getUpdatedAt(), null);
                });

        // When
        TreeMap<String, Object> response = productManager.getProductsByNameContaining(name, page, size, sort);

        // Then
        @SuppressWarnings("unchecked")
        List<GetAllProductsResponse> products = (List<GetAllProductsResponse>) response.get("products");

        assertNotNull(response);
        assertEquals(2, products.size());
        assertEquals(0, response.get("currentPage"));
        assertEquals(2L, response.get("totalItems"));
        assertEquals(1, response.get("totalPages"));
    }

    @Test
    void checkProductInInventory_sufficientQuantity() {
        // Given
        Product product = new Product();
        product.setBarcodeNumber("1234567890123");
        product.setName("Product1");
        product.setQuantity(10);
        product.setUnitPrice(100.0);
        product.setState(true);

        InventoryRequest request = new InventoryRequest("1234567890123", 5);

        when(productRepository.findByBarcodeNumber(anyString())).thenReturn(product);

        // When
        List<InventoryResponse> responses = productManager.checkProductInInventory(List.of(request));

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        InventoryResponse response = responses.get(0);
        assertEquals("Product1", response.getName());
        assertEquals(5, response.getQuantity());
        assertTrue(response.getIsInStock());
        assertEquals(100.0, response.getUnitPrice());
        assertTrue(response.getState());

        verify(productRepository, times(1)).findByBarcodeNumber("1234567890123");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void checkProductInInventory_insufficientQuantity() {
        // Given
        Product product = new Product();
        product.setBarcodeNumber("1234567890123");
        product.setName("Product1");
        product.setQuantity(3);
        product.setUnitPrice(100.0);
        product.setState(true);

        InventoryRequest request = new InventoryRequest("1234567890123", 5);

        when(productRepository.findByBarcodeNumber(anyString())).thenReturn(product);

        // When / Then
        ProductIsNotInStockException thrown = assertThrows(ProductIsNotInStockException.class, () -> productManager.checkProductInInventory(List.of(request)));

        assertEquals("Product is not in stock", thrown.getMessage());

        verify(productRepository, times(1)).findByBarcodeNumber("1234567890123");
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void checkProductInInventory_productNotFound() {
        // Given
        InventoryRequest request = new InventoryRequest("1234567890123", 5);

        when(productRepository.findByBarcodeNumber(anyString())).thenReturn(null);

        // When / Then
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> productManager.checkProductInInventory(List.of(request)));

        assertEquals("Product not found", thrown.getMessage());

        verify(productRepository, times(1)).findByBarcodeNumber("1234567890123");
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void updateProductInInventory_productFound() {
        // Given
        Product product = new Product();
        product.setBarcodeNumber("1234567890123");
        product.setName("Product1");
        product.setQuantity(10);
        product.setUnitPrice(100.0);
        product.setState(true);

        InventoryRequest request = new InventoryRequest("1234567890123", 5);

        when(productRepository.findByBarcodeNumber(anyString())).thenReturn(product);

        // When
        productManager.updateProductInInventory(List.of(request));

        // Then
        verify(productRepository, times(1)).findByBarcodeNumber("1234567890123");
        verify(productRepository, times(1)).save(product);
        assertEquals(15, product.getQuantity());
    }

    @Test
    void updateProductInInventory_productNotFound() {
        // Given
        InventoryRequest request = new InventoryRequest("1234567890123", 5);

        when(productRepository.findByBarcodeNumber(anyString())).thenReturn(null);

        // When / Then
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> productManager.updateProductInInventory(List.of(request)));

        assertEquals("Product not found", thrown.getMessage());

        verify(productRepository, times(1)).findByBarcodeNumber("1234567890123");
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void addProduct_newProduct() {
        // Given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("New Product");
        request.setQuantity(10);
        request.setUnitPrice(100.0);
        request.setDescription("New Product Description");
        request.setState(true);
        request.setImageUrl("http://image.url");
        request.setCreatedBy("Asaf");
        request.setProductCategoryId(1L);

        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("CategoryName");
        when(productCategoryRepository.findById(anyLong())).thenReturn(Optional.of(productCategory));

        Product product = new Product();
        when(productRepository.save(any(Product.class))).thenReturn(product);

        GetAllProductsResponse response = new GetAllProductsResponse();
        response.setProductCategoryName("CategoryName");
        when(modelMapperService.forResponse().map(any(Product.class), eq(GetAllProductsResponse.class))).thenReturn(response);

        // When
        GetAllProductsResponse result = productManager.addProduct(request);

        // Then
        verify(productRepository, times(1)).save(any(Product.class));
        assertNotNull(result);
        assertEquals("CategoryName", result.getProductCategoryName());
    }

    @Test
    void addProduct_existingProductSamePrice() {
        // Given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Existing Product");
        request.setQuantity(5);
        request.setUnitPrice(100.0);
        request.setDescription("Updated Description");
        request.setState(true);
        request.setImageUrl("http://image.url");
        request.setCreatedBy("Asaf");
        request.setProductCategoryId(1L);

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Existing Product");
        existingProduct.setQuantity(10);
        existingProduct.setUnitPrice(100.0);

        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("CategoryName");
        when(productRepository.findByNameIgnoreCase(anyString())).thenReturn(existingProduct);
        when(productCategoryRepository.findById(anyLong())).thenReturn(Optional.of(productCategory));

        Product product = new Product();
        when(productRepository.save(any(Product.class))).thenReturn(product);

        GetAllProductsResponse response = new GetAllProductsResponse();
        response.setProductCategoryName("CategoryName");
        when(modelMapperService.forResponse().map(any(Product.class), eq(GetAllProductsResponse.class))).thenReturn(response);

        // When
        GetAllProductsResponse result = productManager.addProduct(request);

        // Then
        verify(productRepository, times(1)).deleteById(existingProduct.getId());
        verify(productRepository, times(1)).save(any(Product.class));
        assertNotNull(result);
        assertEquals("CategoryName", result.getProductCategoryName());
    }

    @Test
    void addProduct_existingProductDifferentPrice() {
        // Given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Existing Product");
        request.setQuantity(5);
        request.setUnitPrice(150.0); // Different price
        request.setDescription("Updated Description");
        request.setState(true);
        request.setImageUrl("http://image.url");
        request.setCreatedBy("Asaf");
        request.setProductCategoryId(1L);

        Product existingProduct = new Product();
        existingProduct.setName("Existing Product");
        existingProduct.setUnitPrice(100.0);

        when(productRepository.findByNameIgnoreCase(anyString())).thenReturn(existingProduct);

        // When / Then
        EntityAlreadyExistsException thrown = assertThrows(EntityAlreadyExistsException.class, () -> productManager.addProduct(request));

        assertEquals("Product already exists", thrown.getMessage());
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void addProduct_productCategoryNotFound() {
        // Given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("New Product");
        request.setQuantity(10);
        request.setUnitPrice(100.0);
        request.setDescription("New Product Description");
        request.setState(true);
        request.setImageUrl("http://image.url");
        request.setCreatedBy("Asaf");
        request.setProductCategoryId(1L);

        when(productCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> productManager.addProduct(request));

        assertEquals("Product category not found with id: 1", thrown.getMessage());
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void updateProduct_success() {
        // Given
        UpdateProductRequest updateProductRequest = getUpdateProductRequest();

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Existing Product");
        existingProduct.setDescription("Existing Description");
        existingProduct.setQuantity(10);
        existingProduct.setUnitPrice(100.0);
        existingProduct.setState(true);
        existingProduct.setImageUrl("http://existing.image.url");
        existingProduct.setBarcodeNumber("12345678");

        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(1L);
        productCategory.setName("CategoryName");
        existingProduct.setProductCategory(productCategory);

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setQuantity(20);
        updatedProduct.setUnitPrice(200.0);
        updatedProduct.setState(false);
        updatedProduct.setImageUrl("http://updated.image.url");
        updatedProduct.setBarcodeNumber("12345678");
        updatedProduct.setProductCategory(productCategory);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(existingProduct));
        when(modelMapper.map(any(UpdateProductRequest.class), eq(Product.class))).thenReturn(updatedProduct);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(modelMapper.map(any(Product.class), eq(GetAllProductsResponse.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    GetAllProductsResponse response = new GetAllProductsResponse();
                    response.setId(product.getId());
                    response.setName(product.getName());
                    response.setDescription(product.getDescription());
                    response.setQuantity(product.getQuantity());
                    response.setUnitPrice(product.getUnitPrice());
                    response.setState(product.getState());
                    response.setImageUrl(product.getImageUrl());
                    response.setBarcodeNumber(product.getBarcodeNumber());
                    response.setUpdatedAt(product.getUpdatedAt());
                    response.setProductCategoryName(product.getProductCategory().getName());
                    return response;
                });

        // When
        GetAllProductsResponse result = productManager.updateProduct(updateProductRequest);

        // Then
        verify(productRepository, times(1)).findById(anyLong());
        verify(productBusinessRules, times(1)).checkUpdate(any(Product.class), any(Product.class));
        verify(productRepository, times(1)).save(any(Product.class));
        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(20, result.getQuantity());
        assertEquals(200.0, result.getUnitPrice());
        assertFalse(result.getState());
        assertEquals("http://updated.image.url", result.getImageUrl());
        assertEquals("12345678", result.getBarcodeNumber());
        assertEquals("CategoryName", result.getProductCategoryName());
    }

    private static UpdateProductRequest getUpdateProductRequest() {
        UpdateProductRequest updateProductRequest = new UpdateProductRequest();
        updateProductRequest.setId(1L);
        updateProductRequest.setName("Updated Product");
        updateProductRequest.setDescription("Updated Description");
        updateProductRequest.setQuantity(20);
        updateProductRequest.setUnitPrice(200.0);
        updateProductRequest.setState(false);
        updateProductRequest.setImageUrl("http://updated.image.url");
        updateProductRequest.setProductCategoryId(1L);
        updateProductRequest.setCreatedBy("Asaf");
        return updateProductRequest;
    }

    @Test
    void updateProduct_notFound() {
        // Given
        UpdateProductRequest updateProductRequest = new UpdateProductRequest();
        updateProductRequest.setId(1L);

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> productManager.updateProduct(updateProductRequest));
        assertEquals("Product not found", thrown.getMessage());
        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void updateProduct_nameAlreadyExists() {
        // Given
        UpdateProductRequest updateProductRequest = new UpdateProductRequest();
        updateProductRequest.setId(1L);
        updateProductRequest.setName("Updated Product");

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Existing Product");

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(existingProduct));
        when(modelMapper.map(any(UpdateProductRequest.class), eq(Product.class))).thenReturn(existingProduct);
        doThrow(new EntityAlreadyExistsException("Product name already exists")).when(productBusinessRules).checkUpdate(any(Product.class), any(Product.class));

        // When / Then
        EntityAlreadyExistsException thrown = assertThrows(EntityAlreadyExistsException.class, () -> productManager.updateProduct(updateProductRequest));
        assertEquals("Product name already exists", thrown.getMessage());
        verify(productRepository, times(1)).findById(anyLong());
        verify(productBusinessRules, times(1)).checkUpdate(any(Product.class), any(Product.class));
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void deleteProduct_success() {
        // Given
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Existing Product");

        GetAllProductsResponse response = new GetAllProductsResponse();
        response.setId(productId);
        response.setName("Existing Product");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(modelMapper.map(any(Product.class), eq(GetAllProductsResponse.class))).thenReturn(response);

        // When
        GetAllProductsResponse result = productManager.deleteProduct(productId);

        // Then
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).deleteById(productId);
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Existing Product", result.getName());
    }

    @Test
    void deleteProduct_notFound() {
        // Given
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> productManager.deleteProduct(productId));
        assertEquals("Product not found", thrown.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(0)).deleteById(productId);
    }
}
