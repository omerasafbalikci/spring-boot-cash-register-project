package com.toyota.productservice.service.concretes;

import com.toyota.productservice.dao.ProductCategoryRepository;
import com.toyota.productservice.dao.ProductRepository;
import com.toyota.productservice.dao.ProductSpecification;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.InventoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.dto.responses.InventoryResponse;
import com.toyota.productservice.service.abstracts.ProductService;
import com.toyota.productservice.service.rules.ProductBusinessRules;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import com.toyota.productservice.utilities.exceptions.EntityNotFoundException;
import com.toyota.productservice.utilities.exceptions.ProductIsNotInStockException;
import com.toyota.productservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing products.
 */

@Service
@Transactional
@AllArgsConstructor
public class ProductManager implements ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final Logger logger = LogManager.getLogger(ProductService.class);
    private final ModelMapperService modelMapperService;
    private final ProductBusinessRules productBusinessRules;

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
     * Fetches filtered products with pagination and sorting.
     *
     * @param page          the page number to fetch
     * @param size          the number of products per page
     * @param sort          the sort parameters
     * @param id            the ID filter
     * @param barcodeNumber the barcode number filter
     * @param state         the state filter
     * @return a TreeMap containing the filtered products and pagination information
     */
    @Override
    public TreeMap<String, Object> getProductFiltered(int page, int size, String[] sort, Long id, String barcodeNumber,
                                                      Boolean state) {
        logger.info("Fetching all products with pagination. Page: {}, Size: {}, Sort: {}. Filter: id={}, barcodeNumber={}, state={}.", page, size, Arrays.toString(sort), id, barcodeNumber, state);
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        ProductSpecification specification = new ProductSpecification(id, barcodeNumber, state);
        Page<Product> pagePro = this.productRepository.findAll(specification, pagingSort);

        List<GetAllProductsResponse> responses = pagePro.getContent().stream()
                .map(product -> this.modelMapperService.forResponse()
                        .map(product, GetAllProductsResponse.class)).collect(Collectors.toList());
        logger.debug("Get products: Mapped products to response DTOs. Number of products: {}", responses.size());

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("products", responses);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        logger.debug("Get products: Retrieved {} products for page {}. Total items: {}. Total pages: {}.", responses.size(), pagePro.getNumber(), pagePro.getTotalElements(), pagePro.getTotalPages());
        return response;
    }

    /**
     * Fetches products by name containing the specified string with pagination and sorting.
     *
     * @param name  the name to search for
     * @param page  the page number to fetch
     * @param size  the number of products per page
     * @param sort  the sort parameters
     * @return a TreeMap containing the products and pagination information
     */
    @Override
    public TreeMap<String, Object> getProductsByNameContaining(String name, int page, int size, String[] sort) {
        logger.info("Fetching products by name containing '{}', with pagination. Page: {}, Size: {}, Sort: {}.", name, page, size, Arrays.toString(sort));
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        Page<Product> pagePro = this.productRepository.findByNameContainingIgnoreCase(name, pagingSort);

        List<GetAllProductsResponse> responses = pagePro.getContent().stream()
                .map(product -> this.modelMapperService.forResponse()
                        .map(product, GetAllProductsResponse.class)).collect(Collectors.toList());
        logger.debug("Search: Mapped products to response DTOs. Number of products: {}", responses.size());

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("products", responses);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        logger.debug("Search: Retrieved {} products for page {}. Total items: {}. Total pages: {}.", responses.size(), pagePro.getNumber(), pagePro.getTotalElements(), pagePro.getTotalPages());
        return response;
    }

    /**
     * Checks the availability of products in the inventory.
     *
     * @param inventoryRequests a list of inventory requests
     * @return a list of inventory responses
     */
    @Override
    public List<InventoryResponse> checkProductInInventory(List<InventoryRequest> inventoryRequests) {
        List<InventoryResponse> inventoryResponses = new LinkedList<>();
        for (InventoryRequest request : inventoryRequests) {
            String barcodeNumber = request.getBarcodeNumber();
            Integer quantity = request.getQuantity();

            logger.info("Checking product in inventory for barcodeNumber '{}'", barcodeNumber);
            Optional<Product> optionalProduct = this.productRepository.findByBarcodeNumber(barcodeNumber);
            Product product;
            if (optionalProduct.isPresent()) {
                product = optionalProduct.get();
                logger.debug("Check product: Product found in inventory for barcode number '{}'", barcodeNumber);
                if (product.getQuantity() >= quantity) {
                    logger.debug("Sufficient quantity available for product '{}'", barcodeNumber);
                    product.setQuantity(product.getQuantity() - quantity);
                    this.productRepository.save(product);
                } else {
                    logger.warn("Insufficient quantity available for product '{}'", barcodeNumber);
                    throw new ProductIsNotInStockException("Product is not in stock: " + barcodeNumber);
                }
            } else {
                logger.warn("Check product: Product not found in inventory for barcode number '{}'", barcodeNumber);
                throw new EntityNotFoundException("Product not found: " + barcodeNumber);
            }
            InventoryResponse inventoryResponse = getInventoryResponse(product, quantity);
            inventoryResponses.add(inventoryResponse);
        }
        return inventoryResponses;
    }

    /**
     * Creates an inventory response object for a product.
     *
     * @param product  the product entity
     * @param quantity the quantity of the product
     * @return an InventoryResponse object
     */
    private InventoryResponse getInventoryResponse(Product product, Integer quantity) {
        logger.debug("Creating inventory response for product '{}'", product.getName());
        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setName(product.getName());
        inventoryResponse.setQuantity(quantity);
        inventoryResponse.setIsInStock(quantity <= product.getQuantity());
        inventoryResponse.setUnitPrice(product.getUnitPrice());
        inventoryResponse.setState(product.getState());
        return inventoryResponse;
    }

    /**
     * Updates the inventory for a list of products.
     *
     * @param inventoryRequests a list of inventory requests
     */
    @Override
    public void updateProductInInventory(List<InventoryRequest> inventoryRequests) {
        for (InventoryRequest request : inventoryRequests) {
            String barcodeNumber = request.getBarcodeNumber();
            Integer quantity = request.getQuantity();

            logger.info("Updating product in inventory for barcodeNumber '{}'", barcodeNumber);
            Optional<Product> optionalProduct = this.productRepository.findByBarcodeNumber(barcodeNumber);
            Product product;
            if (optionalProduct.isPresent()) {
                product = optionalProduct.get();
                logger.debug("Update product: Product found in inventory for barcode number '{}'", barcodeNumber);
                product.setQuantity(product.getQuantity() + quantity);
                this.productRepository.save(product);
            } else {
                logger.warn("Update product: Product not found in inventory for barcode number '{}'", barcodeNumber);
                throw new EntityNotFoundException("Product not found: " + barcodeNumber);
            }
        }
    }

    /**
     * Adds a new product to the inventory.
     *
     * @param createProductRequest the create product request
     * @return a GetAllProductsResponse object containing the new product details
     */
    @Override
    public GetAllProductsResponse addProduct(CreateProductRequest createProductRequest) {
        logger.info("Adding new product: '{}'.", createProductRequest.getName());
        Optional<Product> optionalProduct = this.productRepository.findByNameIgnoreCase(createProductRequest.getName());
        Product product = new Product();
        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();
            double existingUnitPrice = existingProduct.getUnitPrice();
            double requestUnitPrice = createProductRequest.getUnitPrice();
            double epsilon = 0.0001;
            if (Math.abs(existingUnitPrice - requestUnitPrice) < epsilon) {
                product.setQuantity(existingProduct.getQuantity() + createProductRequest.getQuantity());
                this.productRepository.deleteById(existingProduct.getId());
                logger.debug("Existing product found with name '{}'. Updating quantity and deleting old product.", createProductRequest.getName());
            } else {
                logger.warn("Product with name '{}' already exists with different unit price.", createProductRequest.getName());
                throw new EntityAlreadyExistsException("Product already exists");
            }
        } else {
            product.setQuantity(createProductRequest.getQuantity());
            logger.debug("Creating new product with name '{}'.", createProductRequest.getName());
        }
        product.setBarcodeNumber(UUID.randomUUID().toString().substring(0, 8));
        product.setName(createProductRequest.getName());
        product.setDescription(createProductRequest.getDescription());
        product.setUnitPrice(createProductRequest.getUnitPrice());
        product.setState(createProductRequest.getState());
        product.setImageUrl(createProductRequest.getImageUrl());
        product.setCreatedBy(createProductRequest.getCreatedBy());
        Optional<ProductCategory> optionalProductCategory = this.productCategoryRepository.findById(createProductRequest.getProductCategoryId());
        optionalProductCategory.orElseThrow(() -> {
            logger.warn("No product category found with id '{}'.", createProductRequest.getProductCategoryId());
            return new EntityNotFoundException("Product category not found with id: " + createProductRequest.getProductCategoryId());
        });
        optionalProductCategory.ifPresent(product::setProductCategory);
        product.setUpdatedAt(LocalDateTime.now());
        this.productRepository.save(product);
        logger.debug("New product '{}' added successfully.", createProductRequest.getName());
        return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
    }

    /**
     * Updates an existing product.
     *
     * @param updateProductRequest the update product request
     * @return a GetAllProductsResponse object containing the updated product details
     */
    @Override
    public GetAllProductsResponse updateProduct(UpdateProductRequest updateProductRequest) {
        logger.info("Updating product with id '{}'.", updateProductRequest.getId());
        Product existingProduct = this.productRepository.findById(updateProductRequest.getId()).orElseThrow(() -> {
            logger.warn("No product found with id '{}' to update.", updateProductRequest.getId());
            return new EntityNotFoundException("Product not found");
        });
        Product product = this.modelMapperService.forRequest().map(updateProductRequest, Product.class);
        logger.info("Check if the product name is available?");
        this.productBusinessRules.checkUpdate(product, existingProduct);
        logger.info("Product name does not exist. Proceeding with creating the product.");
        product.setBarcodeNumber(existingProduct.getBarcodeNumber());
        product.setUpdatedAt(LocalDateTime.now());
        this.productRepository.save(product);
        logger.debug("Product with id '{}' updated successfully.", updateProductRequest.getId());
        return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     * @return a GetAllProductsResponse object containing the deleted product details
     */
    @Override
    public GetAllProductsResponse deleteProduct(Long id) {
        logger.info("Deleting product with id '{}'.", id);
        Optional<Product> optionalProduct = this.productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            this.productRepository.deleteById(id);
            logger.debug("Product with id '{}' deleted successfully.", id);
            return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
        } else {
            logger.warn("No product found with id '{}' to delete.", id);
            throw new EntityNotFoundException("Product not found");
        }
    }
}
