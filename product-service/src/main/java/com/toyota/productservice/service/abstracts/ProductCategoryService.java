package com.toyota.productservice.service.abstracts;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;

import java.util.List;

/**
 * Interface for product_category's service class.
 */

public interface ProductCategoryService {
    /**
     * Retrieves all product categories.
     * @return a list of {@link GetAllProductCategoriesResponse} representing all product categories.
     */
    List<GetAllProductCategoriesResponse> getAllCategories();

    /**
     * Retrieves product categories whose names contain the specified string.
     * @param name the string to search for in category names.
     * @return a list of {@link GetAllProductCategoriesResponse} matching the search criteria.
     */
    List<GetAllProductCategoriesResponse> getCategoriesByNameContaining(String name);

    /**
     * Retrieves a product category by its ID.
     *
     * @param id the ID of the category to retrieve.
     * @return the {@link GetAllProductCategoriesResponse} representing the found product category.
     */
    GetAllProductCategoriesResponse getCategoryById(Long id);

    /**
     * Retrieves all products belonging to a specific category.
     *
     * @param categoryId the ID of the category whose products are to be retrieved.
     * @return a list of {@link GetAllProductsResponse} representing the products in the specified category.
     */
    List<GetAllProductsResponse> getProductsByCategoryId(Long categoryId);

    /**
     * Adds a new product category.
     *
     * @param createProductCategoryRequest the details of the category to be created.
     * @return the {@link GetAllProductCategoriesResponse} representing the newly created product category.
     */
    GetAllProductCategoriesResponse addCategory(CreateProductCategoryRequest createProductCategoryRequest);

    /**
     * Updates an existing product category.
     *
     * @param updateProductCategoryRequest the updated details of the category.
     * @return the {@link GetAllProductCategoriesResponse} representing the updated product category.
     */
    GetAllProductCategoriesResponse updateCategory(UpdateProductCategoryRequest updateProductCategoryRequest);

    /**
     * Deletes a product category by its ID.
     *
     * @param id the ID of the category to be deleted.
     * @return the {@link GetAllProductCategoriesResponse} representing the deleted product category.
     */
    GetAllProductCategoriesResponse deleteCategory(Long id);
}
