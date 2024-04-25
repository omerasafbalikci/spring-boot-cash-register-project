package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.service.abstracts.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.TreeMap;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {
    @Autowired
    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<TreeMap<String, Object>> getAllProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = productService.getAllProductsPage(page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<TreeMap<String, Object>> getProductsByNameContaining(
            @RequestParam() String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = productService.getProductsByNameContaining(name, page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/state")
    public ResponseEntity<TreeMap<String, Object>> getProductsByState(
            @RequestParam(defaultValue = "true") Boolean state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = productService.getProductsByState(state, page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/letter")
    public ResponseEntity<TreeMap<String, Object>> getProductsByInitialLetter(
            @RequestParam() char initialLetter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = productService.getProductsByInitialLetter(initialLetter, page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/barcodenumber")
    public GetAllProductsResponse getProductByBarcodeNumber(@RequestParam() String barcodeNumber) {
        return productService.getProductByBarcodeNumber(barcodeNumber);
    }

    @GetMapping("/id")
    public GetAllProductsResponse getProductById(@RequestParam() Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<GetAllProductsResponse> addProduct(@RequestBody @Valid CreateProductRequest createProductRequest) {
        GetAllProductsResponse response = this.productService.addProduct(createProductRequest);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<GetAllProductsResponse> updateProduct(@RequestBody() @Valid UpdateProductRequest updateProductRequest) {
        GetAllProductsResponse response = this.productService.updateProduct(updateProductRequest);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<GetAllProductsResponse> deleteProduct(@RequestParam() Long id) {
        GetAllProductsResponse response = this.productService.deleteProduct(id);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

}
