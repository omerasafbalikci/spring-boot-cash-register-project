package com.toyota.productservice.resource;

import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productcategories")
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryController {
    @Autowired
    private ProductCategoryService productCategoryService;

    @GetMapping()
    public List<GetAllProductCategoriesResponse> getAllCategories() {
        return productCategoryService.getAllCategories();
    }

    @GetMapping("/names/{name}")
    public GetAllProductCategoriesResponse getCategoryByName(@PathVariable("name") String name) {
        return productCategoryService.getCategoryByName(name);
    }

    @GetMapping("/categorynumbers/{categoryNumber}")
    public GetAllProductCategoriesResponse getCategoryByCategoryNumber(@PathVariable("categoryNumber") String categoryNumber) {
        return productCategoryService.getCategoryByCategoryNumber(categoryNumber);
    }

    @GetMapping("/{id}")
    public GetAllProductCategoriesResponse getCategoryById(@PathVariable Long id) {
        return productCategoryService.getCategoryById(id);
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public void addCategory(@RequestBody() @Valid CreateProductCategoryRequest createProductCategoryRequest) {
        this.productCategoryService.addCategory(createProductCategoryRequest);
    }

    @PutMapping
    public void updateCategory(@RequestBody() @Valid UpdateProductCategoryRequest updateProductCategoryRequest) {
        this.productCategoryService.updateCategory(updateProductCategoryRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        this.productCategoryService.deleteCategory(id);
    }

}
