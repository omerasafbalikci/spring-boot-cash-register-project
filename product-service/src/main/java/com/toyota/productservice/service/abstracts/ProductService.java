package com.toyota.productservice.service.abstracts;

import com.toyota.productservice.domain.Product;
import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface ProductService {
    public List<Product> getAllProducts(String[] sort);
    public TreeMap<String, Object> getAllProductsPage(String name, int page, int size, String[] sort);
    GetAllProductsResponse getByIdProduct(Long id);
    void addProduct(CreateProductRequest createProductRequest);
    void updateProduct(UpdateProductRequest updateProductRequest);
    void deleteProduct(Long id);
}
