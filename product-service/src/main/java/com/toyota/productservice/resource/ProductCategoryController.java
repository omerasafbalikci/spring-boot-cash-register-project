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

    @GetMapping("/search")
    public GetAllProductCategoriesResponse findByCategoryNameContaining(@RequestParam() String name) {
        return productCategoryService.findByCategoryNameContaining(name);
    }

    @GetMapping("/categorynumbers")
    public GetAllProductCategoriesResponse getCategoryByCategoryNumber(@RequestParam() String categoryNumber) {
        return productCategoryService.getCategoryByCategoryNumber(categoryNumber);
    }

    @GetMapping("/id")
    public GetAllProductCategoriesResponse getCategoryById(@RequestParam() Long id) {
        return productCategoryService.getCategoryById(id);
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public GetAllProductCategoriesResponse addCategory(@RequestBody() @Valid CreateProductCategoryRequest createProductCategoryRequest) {
        return this.productCategoryService.addCategory(createProductCategoryRequest);
    }

    @PutMapping
    public GetAllProductCategoriesResponse updateCategory(@RequestBody() @Valid UpdateProductCategoryRequest updateProductCategoryRequest) {
        return this.productCategoryService.updateCategory(updateProductCategoryRequest);
    }

    @DeleteMapping("/id")
    public GetAllProductCategoriesResponse deleteCategory(@RequestParam() Long id) {
        return this.productCategoryService.deleteCategory(id);
    }

}
