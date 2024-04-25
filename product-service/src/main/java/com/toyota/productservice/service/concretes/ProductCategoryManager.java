package com.toyota.productservice.service.concretes;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import com.toyota.productservice.service.rules.ProductCategoryBusinessRules;
import com.toyota.productservice.utilities.exceptions.EntityNotFoundException;
import com.toyota.productservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class ProductCategoryManager implements ProductCategoryService {
    @Autowired
    private final ProductCategoryRepository productCategoryRepository;
    private static final Logger logger = LogManager.getLogger(ProductCategoryService.class);
    @Autowired
    private final ModelMapperService modelMapperService;
    @Autowired
    private final ProductCategoryBusinessRules productCategoryBusinessRules;

    @Override
    public List<GetAllProductCategoriesResponse> getAllCategories() {
        logger.info("Fetching all product categories.");
        List<ProductCategory> productCategories = this.productCategoryRepository.findAll();
        logger.debug("Retrieved {} product categories.", productCategories.size());
        List<GetAllProductCategoriesResponse> responses = productCategories.stream()
                .map(productCategory -> this.modelMapperService.forResponse()
                        .map(productCategory, GetAllProductCategoriesResponse.class)).toList();
        logger.info("Retrieved and converted {} product categories to GetAllProductCategoriesResponse.", responses.size());
        return responses;
    }

    @Override
    public List<GetAllProductCategoriesResponse> getCategoriesByNameContaining(String name) {
        logger.info("Fetching product categories by name containing '{}'.", name);
        List<ProductCategory> productCategories = this.productCategoryRepository.findByNameContainingIgnoreCase(name);
        if (!productCategories.isEmpty()) {
            logger.debug("Retrieved {} product categories by name containing '{}'.", productCategories.size(), name);
            List<GetAllProductCategoriesResponse> responses = productCategories.stream()
                    .map(productCategory -> modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class)).toList();
            logger.info("Retrieved and converted {} product categories to GetAllProductCategoriesResponse.", responses.size());
            return responses;
        } else {
            logger.warn("No product categories found by name containing '{}'.", name);
            throw new EntityNotFoundException("Product categories not found");
        }
    }

    @Override
    public GetAllProductCategoriesResponse getCategoryByCategoryNumber(String categoryNumber) {
        logger.info("Fetching product category by category number '{}'.", categoryNumber);
        ProductCategory productCategory = this.productCategoryRepository.findByCategoryNumber(categoryNumber);
        if (productCategory != null) {
            logger.debug("Retrieved product category with category number '{}'.", categoryNumber);
            return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
        } else {
            logger.warn("No product category found with category number '{}'.", categoryNumber);
            throw new EntityNotFoundException("Product category not found");
        }
    }

    @Override
    public GetAllProductCategoriesResponse getCategoryById(Long id) {
        logger.info("Fetching product category by id '{}'.", id);
        ProductCategory productCategory = this.productCategoryRepository.findById(id).orElseThrow(() -> {
            logger.warn("No product category found with id '{}'.", id);
            return new EntityNotFoundException("Product category not found");
        });
        logger.debug("Retrieved product category with id '{}'.", id);
        return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
    }

    @Override
    public GetAllProductCategoriesResponse addCategory(CreateProductCategoryRequest createProductCategoryRequest) {
        logger.info("Adding new product category: '{}'.", createProductCategoryRequest.getName());
        this.productCategoryBusinessRules.checkIfProductCategoryNameExists(createProductCategoryRequest.getName());
        ProductCategory productCategory = this.modelMapperService.forRequest().map(createProductCategoryRequest, ProductCategory.class);
        productCategory.setCategoryNumber(UUID.randomUUID().toString().substring(0, 8));
        productCategory.setUpdatedAt(LocalDateTime.now());
        this.productCategoryRepository.save(productCategory);
        logger.debug("New product category added: '{}'.", createProductCategoryRequest.getName());
        return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
    }

    @Override
    public GetAllProductCategoriesResponse updateCategory(UpdateProductCategoryRequest updateProductCategoryRequest) {
        logger.info("Updating product category with id '{}'.", updateProductCategoryRequest.getId());
        ProductCategory existingProductCategory = this.productCategoryRepository.findById(updateProductCategoryRequest.getId()).orElseThrow(() -> {
            logger.warn("No product category found with id '{}'.", updateProductCategoryRequest.getId());
            return new EntityNotFoundException("Product category not found");
        });
        ProductCategory productCategory = this.modelMapperService.forRequest().map(updateProductCategoryRequest, ProductCategory.class);
        this.productCategoryBusinessRules.checkUpdate(productCategory, existingProductCategory);
        productCategory.setUpdatedAt(LocalDateTime.now());
        this.productCategoryRepository.save(productCategory);
        logger.debug("Product category updated with id '{}'.", updateProductCategoryRequest.getId());
        return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
    }

    @Override
    public GetAllProductCategoriesResponse deleteCategory(Long id) {
        logger.info("Deleting product category with id '{}'.", id);
        ProductCategory productCategory = this.productCategoryRepository.findById(id).orElseThrow(() -> {
            logger.warn("No product category found with id '{}'.", id);
            return new EntityNotFoundException("Product Category not found");
        });
        this.productCategoryRepository.deleteById(id);
        logger.debug("Product category deleted with id '{}'.", id);
        return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
    }
}
