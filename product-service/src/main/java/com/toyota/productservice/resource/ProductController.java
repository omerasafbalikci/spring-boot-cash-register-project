package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.InventoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.dto.responses.InventoryResponse;
import com.toyota.productservice.service.abstracts.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<TreeMap<String, Object>> getAllProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = this.productService.getAllProductsPage(page, size, sort);
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

    @GetMapping("/state")
    public ResponseEntity<TreeMap<String, Object>> getProductsByState(
            @RequestParam(defaultValue = "true") Boolean state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = this.productService.getProductsByState(state, page, size, sort);
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

    @GetMapping("/id")
    public ResponseEntity<GetAllProductsResponse> getProductById(@RequestParam() Long id) {
        GetAllProductsResponse response = this.productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-product-in-inventory")
    public List<InventoryResponse> checkProductInInventory(@RequestBody @Valid List<InventoryRequest> inventoryRequests) {
        return this.productService.checkProductInInventory(inventoryRequests);
    }

    @PostMapping("/update-product-in-inventory")
    public void updateProductInInventory(@RequestBody @Valid List<InventoryRequest> inventoryRequests) {
        this.productService.updateProductInInventory(inventoryRequests);
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
