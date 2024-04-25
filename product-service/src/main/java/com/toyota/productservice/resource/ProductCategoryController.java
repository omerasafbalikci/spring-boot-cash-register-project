package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
    public List<GetAllProductCategoriesResponse> getAllCategories() {
        return productCategoryService.getAllCategories();
    }

    @GetMapping("/search")
    public List<GetAllProductCategoriesResponse> getCategoriesByNameContaining(@RequestParam() String name) {
        return productCategoryService.getCategoriesByNameContaining(name);
    }

    @GetMapping("/categorynumber")
    public GetAllProductCategoriesResponse getCategoryByCategoryNumber(@RequestParam() String categoryNumber) {
        return productCategoryService.getCategoryByCategoryNumber(categoryNumber);
    }

    @GetMapping("/id")
    public GetAllProductCategoriesResponse getCategoryById(@RequestParam() Long id) {
        return productCategoryService.getCategoryById(id);
    }

    @PostMapping("/add")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<GetAllProductCategoriesResponse> addCategory(@RequestBody() @Valid CreateProductCategoryRequest createProductCategoryRequest) {
        GetAllProductCategoriesResponse response = this.productCategoryService.addCategory(createProductCategoryRequest);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<GetAllProductCategoriesResponse> updateCategory(@RequestBody() @Valid UpdateProductCategoryRequest updateProductCategoryRequest) {
        GetAllProductCategoriesResponse response = this.productCategoryService.updateCategory(updateProductCategoryRequest);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<GetAllProductCategoriesResponse> deleteCategory(@RequestParam() Long id) {
        GetAllProductCategoriesResponse response = this.productCategoryService.deleteCategory(id);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

}
