package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.InventoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.dto.responses.InventoryResponse;
import com.toyota.productservice.service.abstracts.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing products.
 */

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * Retrieves filtered products with pagination and sorting.
     *
     * @param page          the page number to retrieve
     * @param size          the number of items per page
     * @param sort          the sorting criteria
     * @param id            the product ID to filter by
     * @param barcodeNumber the barcode number to filter by
     * @param name          the name to filter by
     * @param quantity      the quantity to filter by
     * @param unitPrice     the unit price to filter by
     * @param state         the state to filter by
     * @param createdBy     the creator to filter by
     * @param categoryId    the category ID to filter by
     * @return a ResponseEntity containing a map of the filtered products and pagination details
     */
    @GetMapping("/get-all")
    public ResponseEntity<Map<String, Object>> getProductsFiltered(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(defaultValue = "") Long id,
            @RequestParam(defaultValue = "") String barcodeNumber,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") Integer quantity,
            @RequestParam(defaultValue = "") Double unitPrice,
            @RequestParam(defaultValue = "") Boolean state,
            @RequestParam(defaultValue = "") String createdBy,
            @RequestParam(defaultValue = "") Long categoryId) {
        Map<String, Object> response = this.productService.getProductsFiltered(page, size, sort, id, barcodeNumber, name, quantity, unitPrice, state, createdBy, categoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Checks the inventory for a list of products.
     *
     * @param inventoryRequests the list of inventory requests to check
     * @return a list of InventoryResponse objects indicating the inventory status of each product
     */
    @PostMapping("/check-product-in-inventory")
    public List<InventoryResponse> checkProductInInventory(@RequestBody @Valid List<InventoryRequest> inventoryRequests) {
        return this.productService.checkProductInInventory(inventoryRequests);
    }

    /**
     * Updates the inventory for a list of products.
     *
     * @param inventoryRequests the list of inventory requests to update
     */
    @PostMapping("/update-product-in-inventory")
    public void updateProductInInventory(@RequestBody @Valid List<InventoryRequest> inventoryRequests) {
        this.productService.updateProductInInventory(inventoryRequests);
    }

    /**
     * Adds a new product.
     *
     * @param createProductRequest the request object containing the details of the product to add
     * @return the response object containing the details of the added product
     */
    @PostMapping("/add")
    public ResponseEntity<GetAllProductsResponse> addProduct(@RequestBody @Valid CreateProductRequest createProductRequest) {
        GetAllProductsResponse response = this.productService.addProduct(createProductRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing product.
     *
     * @param updateProductRequest the request object containing the updated details of the product
     * @return the response object containing the details of the updated product
     */
    @PutMapping("/update")
    public ResponseEntity<GetAllProductsResponse> updateProduct(@RequestBody() @Valid UpdateProductRequest updateProductRequest) {
        GetAllProductsResponse response = this.productService.updateProduct(updateProductRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft deletes a product by its ID (marks it as deleted without removing it from the database).
     *
     * @param id the ID of the product to be soft deleted
     * @return a ResponseEntity containing the details of the soft-deleted product
     */
    @DeleteMapping("/delete")
    public ResponseEntity<GetAllProductsResponse> deleteProduct(@RequestParam() Long id) {
        GetAllProductsResponse response = this.productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }
}
