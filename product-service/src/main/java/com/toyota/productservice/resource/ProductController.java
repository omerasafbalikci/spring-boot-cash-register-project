package com.toyota.productservice.resource;

import com.toyota.productservice.domain.Product;
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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@NoArgsConstructor
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/sortedproducts")
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(defaultValue = "id,asc") String[] sort) {
        List<Product> products = productService.getAllProducts(sort);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<TreeMap<String, Object>> getAllProductsPage(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        TreeMap<String, Object> response = productService.getAllProductsPage(name, page, size, sort);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public GetAllProductsResponse getById(@PathVariable Long id) {
        return productService.getByIdProduct(id);
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public void add(@RequestBody @Valid CreateProductRequest createProductRequest) {
        this.productService.addProduct(createProductRequest);
    }

    @PutMapping
    public void update(@RequestBody() UpdateProductRequest updateProductRequest) {
        this.productService.updateProduct(updateProductRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        this.productService.deleteProduct(id);
    }

}
