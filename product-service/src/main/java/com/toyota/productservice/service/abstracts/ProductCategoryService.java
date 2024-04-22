package com.toyota.productservice.service.abstracts;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;

import java.util.List;

public interface ProductCategoryService {
    List<GetAllProductCategoriesResponse> getAllCategories();
    List<GetAllProductCategoriesResponse> findByCategoryNameContaining(String name);
    GetAllProductCategoriesResponse getCategoryByCategoryNumber(String categoryNumber);
    GetAllProductCategoriesResponse getCategoryById(Long id);
    GetAllProductCategoriesResponse addCategory(CreateProductCategoryRequest createProductCategoryRequest);
    GetAllProductCategoriesResponse updateCategory(UpdateProductCategoryRequest updateProductCategoryRequest);
    GetAllProductCategoriesResponse deleteCategory(Long id);
}
