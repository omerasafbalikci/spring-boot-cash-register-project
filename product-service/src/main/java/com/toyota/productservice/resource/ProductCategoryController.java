package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing product categories.
 */

@RestController
@RequestMapping("/api/product-categories")
@AllArgsConstructor
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    /**
     * Retrieves paginated and sorted product categories based on various filter criteria.
     *
     * @param page           the page number to retrieve, default is 0
     * @param size           the number of items per page, default is 3
     * @param sort           the sorting criteria in the format "property,direction", default is "id,asc"
     * @param id             the ID of the category to filter by, default is empty
     * @param categoryNumber the category number to filter by, default is empty
     * @param name           the name to filter by, default is empty
     * @param createdBy      the creator to filter by, default is empty
     * @return a ResponseEntity containing a Map with the filtered categories and pagination details
     */
    @GetMapping("/get-all")
    public ResponseEntity<Map<String, Object>> getCategoriesFiltered(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(defaultValue = "") Long id,
            @RequestParam(defaultValue = "") String categoryNumber,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String createdBy) {
        Map<String, Object> response = this.productCategoryService.getCategoriesFiltered(page, size, sort, id, categoryNumber, name, createdBy);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves paginated and sorted products for a given category ID.
     *
     * @param page       the page number to retrieve, default is 0
     * @param size       the number of items per page, default is 3
     * @param sort       the sorting criteria in the format "property,direction", default is "id,asc"
     * @param categoryId the ID of the product category
     * @return a ResponseEntity containing a Map with the products and pagination details
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProductsByCategoryId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam() Long categoryId) {
        Map<String, Object> response = this.productCategoryService.getProductsByCategoryId(page, size, sort, categoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
     * Marks the product category with the given ID as deleted.
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
