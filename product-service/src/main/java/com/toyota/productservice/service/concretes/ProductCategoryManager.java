package com.toyota.productservice.service.concretes;


import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import com.toyota.productservice.service.rules.ProductCategoryBusinessRules;
import com.toyota.productservice.utilities.exceptions.EntityNotFoundException;
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
        List<ProductCategory> productCategories = this.productCategoryRepository.findAll();

        return productCategories.stream()
                .map(productCategory -> this.modelMapperService.forResponse()
                        .map(productCategory, GetAllProductCategoriesResponse.class)).collect(Collectors.toList());
    }

    @Override
    public List<GetAllProductCategoriesResponse> findByCategoryNameContaining(String name) {
        List<ProductCategory> productCategories = this.productCategoryRepository.findByNameContainingIgnoreCase(name);
        if (!productCategories.isEmpty()) {
            return productCategories.stream()
                    .map(category -> modelMapperService.forResponse().map(category, GetAllProductCategoriesResponse.class))
                    .collect(Collectors.toList());
        } else {
            throw new EntityNotFoundException("Product categories not found");
        }
    }

    @Override
    public GetAllProductCategoriesResponse getCategoryByCategoryNumber(String categoryNumber) {
        ProductCategory productCategory = this.productCategoryRepository.findByCategoryNumber(categoryNumber);
        if (productCategory != null) {
            return this.modelMapperService.forResponse()
                    .map(productCategory, GetAllProductCategoriesResponse.class);
        } else {
            throw new EntityNotFoundException("Product category not found");
        }
    }

    @Override
    public GetAllProductCategoriesResponse getCategoryById(Long id) {
        ProductCategory productCategory = this.productCategoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product category not found"));
        return this.modelMapperService.forResponse()
                .map(productCategory, GetAllProductCategoriesResponse.class);
    }

    @Override
    public GetAllProductCategoriesResponse addCategory(CreateProductCategoryRequest createProductCategoryRequest) {
        this.productCategoryBusinessRules.checkIfProductCategoryNameExists(createProductCategoryRequest.getName());
        ProductCategory productCategory = this.modelMapperService.forRequest().map(createProductCategoryRequest, ProductCategory.class);
        productCategory.setUpdatedAt(LocalDateTime.now());
        this.productCategoryRepository.save(productCategory);
        return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
    }

    @Override
    public GetAllProductCategoriesResponse updateCategory(UpdateProductCategoryRequest updateProductCategoryRequest) {
        ProductCategory existingProductCategory = this.productCategoryRepository.findById(updateProductCategoryRequest.getId()).orElseThrow(() -> new EntityNotFoundException("Product category not found"));
        ProductCategory productCategory = this.modelMapperService.forRequest().map(updateProductCategoryRequest, ProductCategory.class);
        this.productCategoryBusinessRules.checkUpdate(productCategory, existingProductCategory);
        productCategory.setUpdatedAt(LocalDateTime.now());
        this.productCategoryRepository.save(productCategory);
        return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
    }

    @Override
    public GetAllProductCategoriesResponse deleteCategory(Long id) {
        ProductCategory productCategory = this.productCategoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product Category not found"));
        this.productCategoryRepository.deleteById(id);
        return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
    }
}
