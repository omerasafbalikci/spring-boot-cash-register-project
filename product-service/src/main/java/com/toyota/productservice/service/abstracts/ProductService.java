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
    public TreeMap<String, Object> getAllProductsPage(String name, int page, int size, String[] sort);
    public TreeMap<String, Object> findByState(boolean isState, int page, int size, String[] sort);
    public GetAllProductsResponse getProductByBarcodeNumber(String barcodeNumber);
    public GetAllProductsResponse getProductById(Long id);
    void addProduct(CreateProductRequest createProductRequest);
    void updateProduct(UpdateProductRequest updateProductRequest);
    void deleteProduct(Long id);
}
