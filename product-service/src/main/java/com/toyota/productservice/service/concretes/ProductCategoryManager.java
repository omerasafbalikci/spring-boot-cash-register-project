package com.toyota.productservice.service.concretes;


import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import com.toyota.productservice.service.rules.ProductCategoryBusinessRules;
import com.toyota.productservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryManager implements ProductCategoryService {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ModelMapperService modelMapperService;
    @Autowired
    private ProductCategoryBusinessRules productCategoryBusinessRules;

    @Override
    public List<GetAllProductCategoriesResponse> getAllCategories() {
        List<ProductCategory> productCategories = productCategoryRepository.findAll();

        return productCategories.stream()
                .map(productCategory -> this.modelMapperService.forResponse()
                        .map(productCategory, GetAllProductCategoriesResponse.class)).collect(Collectors.toList());
    }

    @Override
    public GetAllProductCategoriesResponse getCategoryByName(String name) {
        ProductCategory productCategory = this.productCategoryRepository.findByNameIgnoreCase(name);

        return this.modelMapperService.forResponse()
                .map(productCategory, GetAllProductCategoriesResponse.class);
    }

    @Override
    public GetAllProductCategoriesResponse getCategoryByCategoryNumber(String categoryNumber) {
        ProductCategory productCategory = this.productCategoryRepository.findByCategoryNumber(categoryNumber);

        return this.modelMapperService.forResponse()
                .map(productCategory, GetAllProductCategoriesResponse.class);
    }

    @Override
    public GetAllProductCategoriesResponse getCategoryById(Long id) {
        ProductCategory productCategory = this.productCategoryRepository.findById(id).orElseThrow();

        return this.modelMapperService.forResponse()
                .map(productCategory, GetAllProductCategoriesResponse.class);
    }

    @Override
    public void addCategory(CreateProductCategoryRequest createProductCategoryRequest) {
        this.productCategoryBusinessRules.checkIfProductCategoryNameExists(createProductCategoryRequest.getName());
        ProductCategory productCategory = this.modelMapperService.forRequest().map(createProductCategoryRequest, ProductCategory.class);
        productCategory.setUpdatedAt(LocalDateTime.now());
        this.productCategoryRepository.save(productCategory);
    }

    @Override
    public void updateCategory(UpdateProductCategoryRequest updateProductCategoryRequest) {
        ProductCategory existingProductCategory = this.productCategoryRepository.findById(updateProductCategoryRequest.getId()).orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));
        ProductCategory productCategory = this.modelMapperService.forRequest().map(updateProductCategoryRequest, ProductCategory.class);
        productCategoryBusinessRules.checkUpdate(productCategory, existingProductCategory);
        productCategory.setUpdatedAt(LocalDateTime.now());
        this.productCategoryRepository.save(productCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        this.productCategoryRepository.deleteById(id);
    }
}
