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

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

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

    @GetMapping("/search")
    public ResponseEntity<TreeMap<String, Object>> getProductsByNameContaining(
            @RequestParam() String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = this.productService.getProductsByNameContaining(name, page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/letter")
    public ResponseEntity<TreeMap<String, Object>> getProductsByInitialLetter(
            @RequestParam() String initialLetter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = this.productService.getProductsByInitialLetter(initialLetter, page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/check-product-in-inventory")
    public List<InventoryResponse> checkProductInInventory(@RequestBody @Valid List<InventoryRequest> inventoryRequests) {
        return this.productService.checkProductInInventory(inventoryRequests);
    }

    @PostMapping("/update-product-in-inventory")
    public void updateProductInInventory(@RequestBody @Valid List<InventoryRequest> inventoryRequests) {
        this.productService.updateProductInInventory(inventoryRequests);
    }

    @PostMapping("/returned-product")
    public void returnedProduct(@RequestBody @Valid InventoryRequest inventoryRequest) {
        this.productService.returnedProduct(inventoryRequest);
    }

    @PostMapping("/add")
    public ResponseEntity<GetAllProductsResponse> addProduct(@RequestBody @Valid CreateProductRequest createProductRequest) {
        GetAllProductsResponse response = this.productService.addProduct(createProductRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<GetAllProductsResponse> updateProduct(@RequestBody() @Valid UpdateProductRequest updateProductRequest) {
        GetAllProductsResponse response = this.productService.updateProduct(updateProductRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<GetAllProductsResponse> deleteProduct(@RequestParam() Long id) {
        GetAllProductsResponse response = this.productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }

}
