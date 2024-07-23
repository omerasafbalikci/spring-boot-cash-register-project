package com.toyota.productservice.service.concretes;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.dao.ProductCategorySpecification;
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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
     * Retrieves filtered and paginated product categories.
     *
     * @param page           the page number to retrieve
     * @param size           the number of items per page
     * @param sort           the sorting criteria
     * @param id             the ID to filter by
     * @param categoryNumber the category number to filter by
     * @param name           the name to filter by
     * @param createdBy      the creator to filter by
     * @return a Map containing the filtered product categories and pagination details
     */
    @Override
    public Map<String, Object> getCategoriesFiltered(int page, int size, String[] sort, Long id, String categoryNumber,
                                                     String name, String createdBy) {
        logger.info("Fetching all product categories with pagination. Page: {}, Size: {}, Sort: {}. Filter: id={}, categoryNumber={}, name={}, createdBy={}.", page, size, Arrays.toString(sort), id, categoryNumber, name, createdBy);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        ProductCategorySpecification specification = new ProductCategorySpecification(id, categoryNumber, name, createdBy);
        Page<ProductCategory> categoryPage = this.productCategoryRepository.findAll(specification, pagingSort);

        List<GetAllProductCategoriesResponse> responses = categoryPage.getContent().stream()
                .map(productCategory -> this.modelMapperService.forResponse()
                        .map(productCategory, GetAllProductCategoriesResponse.class)).collect(Collectors.toList());
        logger.debug("Get product categories: Mapped product categories to response DTOs. Number of product categories: {}", responses.size());

        Map<String, Object> response = new HashMap<>();
        response.put("productCategories", responses);
        response.put("currentPage", categoryPage.getNumber());
        response.put("totalItems", categoryPage.getTotalElements());
        response.put("totalPages", categoryPage.getTotalPages());
        logger.debug("Get product categories: Retrieved {} products for page {}. Total items: {}. Total pages: {}.", responses.size(), categoryPage.getNumber(), categoryPage.getTotalElements(), categoryPage.getTotalPages());
        return response;
    }

    /**
     * Determines the sorting direction.
     *
     * @param direction the direction to sort (asc or desc)
     * @return the Sort.Direction enum value
     */
    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    /**
     * Creates a list of Sort.Order objects based on the provided sort parameters.
     *
     * @param sort the sort parameters
     * @return a list of Sort.Order objects
     */
    private List<Sort.Order> getOrder(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return orders;
    }

    /**
     * Retrieves products by category ID with pagination and sorting.
     *
     * @param page       the page number to retrieve
     * @param size       the number of items per page
     * @param sort       the sorting criteria
     * @param categoryId the ID of the product category
     * @return a Map containing the products and pagination details
     * @throws EntityNotFoundException if no product category with the given ID is found
     */
    @Override
    public Map<String, Object> getProductsByCategoryId(int page, int size, String[] sort, Long categoryId) {
        logger.info("Fetching products by category id '{}' with pagination. Page: {}, Size: {}, Sort: {}.", categoryId, page, size, Arrays.toString(sort));
        Optional<ProductCategory> optionalProductCategory = this.productCategoryRepository.findByIdAndDeletedFalse(categoryId);
        if (optionalProductCategory.isPresent()) {
            ProductCategory productCategory = optionalProductCategory.get();
            List<Product> products = productCategory.getProducts();

            Pageable pageable = PageRequest.of(page, size, Sort.by(getOrder(sort)));

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), products.size());

            if (start > products.size()) {
                start = products.size();
                end = products.size();
            }

            Page<Product> pagePro = new PageImpl<>(products.subList(start, end), pageable, products.size());

            List<GetAllProductsResponse> responses = pagePro.getContent().stream()
                    .filter(product -> !product.isDeleted())
                    .map(product -> modelMapperService.forResponse().map(product, GetAllProductsResponse.class))
                    .collect(Collectors.toList());
            logger.debug("Mapped products to response DTOs. Number of products: {}", responses.size());

            Map<String, Object> response = new HashMap<>();
            response.put("products", responses);
            response.put("currentPage", pagePro.getNumber());
            response.put("totalItems", pagePro.getTotalElements());
            response.put("totalPages", pagePro.getTotalPages());
            logger.debug("Retrieved {} products for category id '{}'. Total items: {}. Total pages: {}.", responses.size(), categoryId, pagePro.getTotalElements(), pagePro.getTotalPages());
            return response;
        } else {
            logger.warn("No product category found with id '{}'.", categoryId);
            throw new EntityNotFoundException("Product category not found");
        }
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
        Optional<ProductCategory> optionalProductCategory = this.productCategoryRepository.findByIdAndDeletedFalse(updateProductCategoryRequest.getId());
        if (optionalProductCategory.isPresent()) {
            ProductCategory productCategory = optionalProductCategory.get();
            if (this.productCategoryRepository.existsByNameIgnoreCaseAndDeletedIsFalse(updateProductCategoryRequest.getName()) && !productCategory.getName().equals(updateProductCategoryRequest.getName())) {
                logger.warn("Product category name '{}' already exists.", productCategory.getName());
                throw new EntityAlreadyExistsException("Product category name already exists");
            }
            this.productCategoryBusinessRules.checkUpdate(updateProductCategoryRequest, productCategory);
            logger.info("Product category name does not exist. Proceeding with updating the product category.");
            this.productCategoryRepository.save(productCategory);
            logger.debug("Product category updated with id '{}'.", updateProductCategoryRequest.getId());
            return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
        } else {
            logger.warn("No product category found with id '{}' to update.", updateProductCategoryRequest.getId());
            throw new EntityNotFoundException("Product category not found");
        }
    }

    /**
     * Marks the product category with the given ID as deleted.
     *
     * @param id the ID of the product category to delete
     * @return the response object containing the details of the deleted product category
     * @throws EntityNotFoundException if no product category with the given ID is found
     */
    @Override
    public GetAllProductCategoriesResponse deleteCategory(Long id) {
        logger.info("Deleting product category with id '{}'.", id);
        Optional<ProductCategory> optionalProductCategory = this.productCategoryRepository.findByIdAndDeletedFalse(id);
        if (optionalProductCategory.isPresent()) {
            ProductCategory productCategory = optionalProductCategory.get();
            productCategory.setUpdatedAt(LocalDateTime.now());
            productCategory.setDeleted(true);
            this.productCategoryRepository.save(productCategory);
            logger.debug("Product category deleted with id '{}'.", id);
            return this.modelMapperService.forResponse().map(productCategory, GetAllProductCategoriesResponse.class);
        } else {
            logger.warn("No product category found with id '{}' to delete.", id);
            throw new EntityNotFoundException("Product category not found");
        }
    }
}
