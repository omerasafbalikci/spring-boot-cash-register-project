package com.toyota.productservice.service.abstracts;

import com.toyota.productservice.domain.Product;
import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public interface ProductService {
    public TreeMap<String, Object> getAllProductsPage(int page, int size, String[] sort);
    public TreeMap<String, Object> findByProductNameContaining(String name, int page, int size, String[] sort);
    public TreeMap<String, Object> findByProductState(Boolean isState, int page, int size, String[] sort);
    public GetAllProductsResponse getProductByBarcodeNumber(String barcodeNumber);
    public GetAllProductsResponse getProductById(Long id);
    public void addProduct(CreateProductRequest createProductRequest);
    public void updateProduct(UpdateProductRequest updateProductRequest);
    public void deleteProduct(Long id);
}
