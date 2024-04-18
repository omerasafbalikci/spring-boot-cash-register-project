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
    public List<GetAllProductCategoriesResponse> getAll() {
        List<ProductCategory> productCategories = productCategoryRepository.findAll();

        List<GetAllProductCategoriesResponse> productCategoriesResponse = productCategories.stream()
                .map(productCategory -> this.modelMapperService.forResponse()
                        .map(productCategory, GetAllProductCategoriesResponse.class)).collect(Collectors.toList());

        return productCategoriesResponse;
    }

    @Override
    public GetAllProductCategoriesResponse getById(Long id) {
        ProductCategory productCategory = this.productCategoryRepository.findById(id).orElseThrow();

        GetAllProductCategoriesResponse response = this.modelMapperService.forResponse()
                .map(productCategory, GetAllProductCategoriesResponse.class);

        return response;
    }

    @Override
    public void add(CreateProductCategoryRequest createProductCategoryRequest) {
        this.productCategoryBusinessRules.checkIfProductCategoryNameExists(createProductCategoryRequest.getName());
        ProductCategory productCategory = this.modelMapperService.forRequest().map(createProductCategoryRequest, ProductCategory.class);
        this.productCategoryRepository.save(productCategory);
    }

    @Override
    public void update(UpdateProductCategoryRequest updateProductCategoryRequest) {
        ProductCategory productCategory = this.modelMapperService.forRequest().map(updateProductCategoryRequest, ProductCategory.class);
        this.productCategoryRepository.save(productCategory);
    }

    @Override
    public void delete(Long id) {
        this.productCategoryRepository.deleteById(id);
    }
}
