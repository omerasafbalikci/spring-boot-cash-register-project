package com.toyota.productservice.service.abstracts;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;

import java.util.Map;

/**
 * Interface for product_category's service class.
 */

public interface ProductCategoryService {
    /**
     * Retrieves filtered and paginated product categories.
     *
     * @param page           the page number to retrieve
     * @param size           the number of items per page
     * @param sort           the sorting criteria
     * @param id             the ID to filter by
     * @param categoryNumber the category number to filter by
     * @param name           the name to filter by
     * @param createdBy      the creator to filter by
     * @return a Map containing the filtered product categories and pagination details
     */
    Map<String, Object> getCategoriesFiltered(int page, int size, String[] sort, Long id, String categoryNumber,
                                              String name, String createdBy);

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
     * Marks the product category with the given ID as deleted.
     *
     * @param id the ID of the category to be deleted.
     * @return the {@link GetAllProductCategoriesResponse} representing the deleted product category.
     */
    GetAllProductCategoriesResponse deleteCategory(Long id);
}
