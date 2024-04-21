package com.toyota.productservice.service.abstracts;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;

import java.util.List;

public interface ProductCategoryService {
    public List<GetAllProductCategoriesResponse> getAllCategories();
    public GetAllProductCategoriesResponse getCategoryByName(String name);
    public GetAllProductCategoriesResponse getCategoryByCategoryNumber(String categoryNumber);
    public GetAllProductCategoriesResponse getCategoryById(Long id);
    public void addCategory(CreateProductCategoryRequest createProductCategoryRequest);
    public void updateCategory(UpdateProductCategoryRequest updateProductCategoryRequest);
    public void deleteCategory(Long id);
}
