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
    public List<GetAllProductCategoriesResponse> getAll() {
        return productCategoryService.getAll();
    }

    @GetMapping("/{id}")
    public GetAllProductCategoriesResponse getById(@PathVariable Long id) {
        return productCategoryService.getById(id);
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public void add(@RequestBody() @Valid CreateProductCategoryRequest createProductCategoryRequest) {
        this.productCategoryService.add(createProductCategoryRequest);
    }

    @PutMapping
    public void update(@RequestBody() UpdateProductCategoryRequest updateProductCategoryRequest) {
        this.productCategoryService.update(updateProductCategoryRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        this.productCategoryService.delete(id);
    }

}
