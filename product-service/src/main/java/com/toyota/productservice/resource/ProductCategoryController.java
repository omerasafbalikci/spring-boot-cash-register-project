package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing product categories.
 */

@RestController
@RequestMapping("/api/product-categories")
@AllArgsConstructor
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    /**
     * Retrieves all product categories.
     *
     * @return a ResponseEntity containing the list of all product categories
     */
    @GetMapping()
    public ResponseEntity<List<GetAllProductCategoriesResponse>> getAllCategories() {
        List<GetAllProductCategoriesResponse> responses = this.productCategoryService.getAllCategories();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /**
     * Retrieves product categories by name containing a specified string.
     *
     * @param name the string to search for in category names
     * @return a ResponseEntity containing the list of matching product categories
     */
    @GetMapping("/search")
    public ResponseEntity<List<GetAllProductCategoriesResponse>> getCategoriesByNameContaining(@RequestParam() String name) {
        List<GetAllProductCategoriesResponse> responses = this.productCategoryService.getCategoriesByNameContaining(name);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /**
     * Retrieves a product category by its category number.
     *
     * @param categoryNumber the category number of the product category to retrieve
     * @return a ResponseEntity containing the matching product category
     */
    @GetMapping("/category-number")
    public ResponseEntity<GetAllProductCategoriesResponse> getCategoryByCategoryNumber(@RequestParam() String categoryNumber) {
        GetAllProductCategoriesResponse response = this.productCategoryService.getCategoryByCategoryNumber(categoryNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a product category by its ID.
     *
     * @param id the ID of the product category to retrieve
     * @return a ResponseEntity containing the matching product category
     */
    @GetMapping("/id")
    public ResponseEntity<GetAllProductCategoriesResponse> getCategoryById(@RequestParam() Long id) {
        GetAllProductCategoriesResponse response = this.productCategoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all products in a specified category.
     *
     * @param categoryId the ID of the category whose products to retrieve
     * @return a ResponseEntity containing the list of products in the specified category
     */
    @GetMapping("/products")
    public ResponseEntity<List<GetAllProductsResponse>> getProductsByCategoryId(@RequestParam() Long categoryId) {
        List<GetAllProductsResponse> responses = this.productCategoryService.getProductsByCategoryId(categoryId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /**
     * Adds a new product category.
     *
     * @param createProductCategoryRequest the request object containing the details of the category to add
     * @return a ResponseEntity containing the details of the added product category
     */
    @PostMapping("/add")
    public ResponseEntity<GetAllProductCategoriesResponse> addCategory(@RequestBody() @Valid CreateProductCategoryRequest createProductCategoryRequest) {
        GetAllProductCategoriesResponse response = this.productCategoryService.addCategory(createProductCategoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing product category.
     *
     * @param updateProductCategoryRequest the request object containing the updated details of the category
     * @return a ResponseEntity containing the details of the updated product category
     */
    @PutMapping("/update")
    public ResponseEntity<GetAllProductCategoriesResponse> updateCategory(@RequestBody() @Valid UpdateProductCategoryRequest updateProductCategoryRequest) {
        GetAllProductCategoriesResponse response = this.productCategoryService.updateCategory(updateProductCategoryRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a product category.
     *
     * @param id the ID of the category to delete
     * @return a ResponseEntity containing the details of the deleted product category
     */
    @DeleteMapping("/delete")
    public ResponseEntity<GetAllProductCategoriesResponse> deleteCategory(@RequestParam() Long id) {
        GetAllProductCategoriesResponse response = this.productCategoryService.deleteCategory(id);
        return ResponseEntity.ok(response);
    }
}
