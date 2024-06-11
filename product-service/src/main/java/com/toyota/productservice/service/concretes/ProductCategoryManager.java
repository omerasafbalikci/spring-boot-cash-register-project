package com.toyota.productservice.service.concretes;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductCategoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductCategoryRequest;
import com.toyota.productservice.dto.responses.GetAllProductCategoriesResponse;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.service.abstracts.ProductCategoryService;
import com.toyota.productservice.service.rules.ProductCategoryBusinessRules;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import com.toyota.productservice.utilities.exceptions.EntityNotFoundException;
import com.toyota.productservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing product categories.
 */

@Service
@Transactional
@AllArgsConstructor
public class ProductCategoryManager implements ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;
    private final Logger logger = LogManager.getLogger(ProductCategoryService.class);
    private final ModelMapperService modelMapperService;
    private final ProductCategoryBusinessRules productCategoryBusinessRules;

    /**
     * Fetches all product categories.
     *
     * @return a list of GetAllProductCategoriesResponse objects
     */
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

    /**
     * Fetches product categories by name containing the specified string.
     *
     * @param name the name string to search for
     * @return a list of GetAllProductCategoriesResponse objects
     */
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
            throw new EntityNotFoundException("Product category not found for name: " + name);
        }
    }

    /**
     * Fetches a product category by its ID.
     *
     * @param id the ID of the product category
     * @return a GetAllProductCategoriesResponse object
     */
    @Override
    public GetAllProductCategoriesResponse getCategoryById(Long id) {
        logger.info("Fetching product category by id '{}'.", id);
        ProductCategory productCategory = this.productCategoryRepository.findById(id).orElseThrow(() -> {
            logger.warn("No product category found with id '{}'.", id);
            return new EntityNotFoundException("Product category not found for id: " + id);
        });
        logger.debug("Retrieved product category with id '{}'.", id);
        return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
    }

    /**
     * Fetches products by their category ID.
     *
     * @param categoryId the ID of the category
     * @return a list of GetAllProductsResponse objects
     */
    @Override
    public List<GetAllProductsResponse> getProductsByCategoryId(Long categoryId) {
        logger.info("Fetching products by category id '{}'.", categoryId);
        ProductCategory productCategory = this.productCategoryRepository.findById(categoryId).orElseThrow(() -> {
            logger.warn("No product category found with id '{}'.", categoryId);
            return new EntityNotFoundException("Product category not found");
        });
        logger.debug("Retrieved products for category id '{}'.", categoryId);
        List<Product> products = productCategory.getProducts();
        List<GetAllProductsResponse> responses = products.stream()
                .map(product -> modelMapperService.forResponse().map(product, GetAllProductsResponse.class)).toList();
        logger.info("Retrieved and converted {} product to GetAllProductsResponse.", responses.size());
        return responses;
    }

    /**
     * Adds a new product category.
     *
     * @param createProductCategoryRequest the create product category request
     * @return a GetAllProductCategoriesResponse object containing the new product category details
     */
    @Override
    public GetAllProductCategoriesResponse addCategory(CreateProductCategoryRequest createProductCategoryRequest) {
        logger.info("Adding new product category: '{}'.", createProductCategoryRequest.getName());
        try {
            this.productCategoryBusinessRules.checkIfProductCategoryNameExists(createProductCategoryRequest.getName());
        } catch (EntityAlreadyExistsException e) {
            logger.warn("Product category '{}' already exists.", createProductCategoryRequest.getName());
            throw new EntityAlreadyExistsException("Product category already exists");
        }
        ProductCategory productCategory = this.modelMapperService.forRequest().map(createProductCategoryRequest, ProductCategory.class);
        productCategory.setCategoryNumber(UUID.randomUUID().toString().substring(0, 8));
        productCategory.setUpdatedAt(LocalDateTime.now());
        this.productCategoryRepository.save(productCategory);
        logger.debug("New product category added: '{}'.", createProductCategoryRequest.getName());
        return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
    }

    /**
     * Updates an existing product category.
     *
     * @param updateProductCategoryRequest the update product category request
     * @return a GetAllProductCategoriesResponse object containing the updated product category details
     */
    @Override
    public GetAllProductCategoriesResponse updateCategory(UpdateProductCategoryRequest updateProductCategoryRequest) {
        logger.info("Updating product category with id '{}'.", updateProductCategoryRequest.getId());
        Optional<ProductCategory> optionalProductCategory = this.productCategoryRepository.findById(updateProductCategoryRequest.getId());
        if (optionalProductCategory.isPresent()) {
            ProductCategory productCategory = optionalProductCategory.get();
            if (this.productCategoryRepository.existsByNameIgnoreCase(updateProductCategoryRequest.getName()) && !productCategory.getName().equals(updateProductCategoryRequest.getName())) {
                logger.warn("Product category name '{}' already exists.", productCategory.getName());
                throw new EntityAlreadyExistsException("Product category name already exists");
            }
            this.productCategoryBusinessRules.checkUpdate(updateProductCategoryRequest, productCategory);
            logger.info("Product category name does not exist. Proceeding with updating the product category.");
            this.productCategoryRepository.save(productCategory);
            logger.debug("Product category updated with id '{}'.", updateProductCategoryRequest.getId());
            return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
        } else {
            logger.warn("No product category found with id '{}'.", updateProductCategoryRequest.getId());
            throw new EntityNotFoundException("Product category not found");
        }
    }

    /**
     * Deletes a product category by its ID.
     *
     * @param id the ID of the product category to delete
     * @return a GetAllProductCategoriesResponse object containing the deleted product category details
     */
    @Override
    public GetAllProductCategoriesResponse deleteCategory(Long id) {
        logger.info("Deleting product category with id '{}'.", id);
        ProductCategory productCategory = this.productCategoryRepository.findById(id).orElseThrow(() -> {
            logger.warn("No product category found with id '{}'.", id);
            return new EntityNotFoundException("Product category not found");
        });
        this.productCategoryRepository.deleteById(id);
        logger.debug("Product category deleted with id '{}'.", id);
        return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
    }
}
