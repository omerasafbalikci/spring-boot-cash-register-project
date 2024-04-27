package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productcategories")
@AllArgsConstructor
public class ProductCategoryController {
    @Autowired
    private final ProductCategoryService productCategoryService;

    @GetMapping()
    public ResponseEntity<List<GetAllProductCategoriesResponse>> getAllCategories() {
        List<GetAllProductCategoriesResponse> responses = productCategoryService.getAllCategories();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GetAllProductCategoriesResponse>> getCategoriesByNameContaining(@RequestParam() String name) {
        List<GetAllProductCategoriesResponse> responses = productCategoryService.getCategoriesByNameContaining(name);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/categorynumber")
    public ResponseEntity<GetAllProductCategoriesResponse> getCategoryByCategoryNumber(@RequestParam() String categoryNumber) {
        GetAllProductCategoriesResponse response = productCategoryService.getCategoryByCategoryNumber(categoryNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id")
    public ResponseEntity<GetAllProductCategoriesResponse> getCategoryById(@RequestParam() Long id) {
        GetAllProductCategoriesResponse response = productCategoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<GetAllProductCategoriesResponse> addCategory(@RequestBody() @Valid CreateProductCategoryRequest createProductCategoryRequest) {
        GetAllProductCategoriesResponse response = this.productCategoryService.addCategory(createProductCategoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<GetAllProductCategoriesResponse> updateCategory(@RequestBody() @Valid UpdateProductCategoryRequest updateProductCategoryRequest) {
        GetAllProductCategoriesResponse response = this.productCategoryService.updateCategory(updateProductCategoryRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<GetAllProductCategoriesResponse> deleteCategory(@RequestParam() Long id) {
        GetAllProductCategoriesResponse response = this.productCategoryService.deleteCategory(id);
        return ResponseEntity.ok(response);
    }

}
