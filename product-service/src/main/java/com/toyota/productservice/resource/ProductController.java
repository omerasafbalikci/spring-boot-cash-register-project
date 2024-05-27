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
import java.util.TreeMap;

/**
 * REST controller for managing products.
 */

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * Retrieves a filtered list of products.
     *
     * @param page the page number to retrieve (default is 0)
     * @param size the size of the page (default is 3)
     * @param sort the sort criteria (default is "id,asc")
     * @param id optional filter by product ID
     * @param barcodeNumber optional filter by barcode number
     * @param state optional filter by state
     * @return a TreeMap containing the filtered products
     */
    @GetMapping()
    public ResponseEntity<TreeMap<String, Object>> getProductFiltered(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(defaultValue = "") Long id,
            @RequestParam(defaultValue = "") String barcodeNumber,
            @RequestParam(defaultValue = "") Boolean state) {
        TreeMap<String, Object> response = this.productService.getProductFiltered(page, size, sort, id, barcodeNumber, state);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Searches for products by name.
     *
     * @param name the name to search for
     * @param page the page number to retrieve (default is 0)
     * @param size the size of the page (default is 3)
     * @param sort the sort criteria (default is "id,asc")
     * @return a TreeMap containing the products with names containing the specified name
     */
    @GetMapping("/search")
    public ResponseEntity<TreeMap<String, Object>> getProductsByNameContaining(
            @RequestParam() String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = this.productService.getProductsByNameContaining(name, page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves products by their initial letter.
     *
     * @param initialLetter the initial letter to filter by
     * @param page the page number to retrieve (default is 0)
     * @param size the size of the page (default is 3)
     * @param sort the sort criteria (default is "id,asc")
     * @return a TreeMap containing the products starting with the specified initial letter
     */
    @GetMapping("/letter")
    public ResponseEntity<TreeMap<String, Object>> getProductsByInitialLetter(
            @RequestParam() String initialLetter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = this.productService.getProductsByInitialLetter(initialLetter, page, size, sort);
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
     * Deletes a product.
     *
     * @param id the ID of the product to delete
     * @return the response object containing the details of the deleted product
     */
    @DeleteMapping("/delete")
    public ResponseEntity<GetAllProductsResponse> deleteProduct(@RequestParam() Long id) {
        GetAllProductsResponse response = this.productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }

}
