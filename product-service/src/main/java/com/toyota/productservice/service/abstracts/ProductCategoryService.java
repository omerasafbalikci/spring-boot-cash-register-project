package com.toyota.productservice.service.abstracts;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;

import java.util.List;

public interface ProductCategoryService {
    List<GetAllProductCategoriesResponse> getAll();
    GetAllProductCategoriesResponse getById(Long id);
    void add(CreateProductCategoryRequest createProductCategoryRequest);
    void update(UpdateProductCategoryRequest updateProductCategoryRequest);
    void delete(Long id);
}
