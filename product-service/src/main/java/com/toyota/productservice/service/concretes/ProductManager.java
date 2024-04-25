package com.toyota.productservice.service.concretes;

import com.toyota.productservice.dao.ProductRepository;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.service.abstracts.ProductService;
import com.toyota.productservice.service.rules.ProductBusinessRules;
import com.toyota.productservice.utilities.exceptions.EntityAlreadyExistsException;
import com.toyota.productservice.utilities.exceptions.EntityNotFoundException;
import com.toyota.productservice.utilities.mappers.ModelMapperService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ProductManager implements ProductService {
    @Autowired
    private final ProductRepository productRepository;
    private static final Logger logger = LogManager.getLogger(ProductService.class);
    @Autowired
    private final ModelMapperService modelMapperService;
    @Autowired
    private final ProductBusinessRules productBusinessRules;

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

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

    @Override
    public TreeMap<String, Object> getAllProductsPage(int page, int size, String[] sort) {
        logger.info("Fetching all products with pagination. Page: {}, Size: {}, Sort: {}.", page, size, Arrays.toString(sort));
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        Page<Product> pagePro;
        pagePro = this.productRepository.findAll(pagingSort);

        List<GetAllProductsResponse> responses = pagePro.getContent().stream()
                .map(product -> this.modelMapperService.forResponse()
                        .map(product, GetAllProductsResponse.class)).collect(Collectors.toList());

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("products", responses);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        logger.debug("Retrieved {} products for page {}. Total items: {}. Total pages: {}.", responses.size(), pagePro.getNumber(), pagePro.getTotalElements(), pagePro.getTotalPages());
        return response;
    }

    @Override
    public TreeMap<String, Object> getProductsByNameContaining(String name, int page, int size, String[] sort) {
        logger.info("Fetching products by name containing '{}', with pagination. Page: {}, Size: {}, Sort: {}.", name, page, size, Arrays.toString(sort));
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        Page<Product> pagePro = this.productRepository.findByNameContainingIgnoreCase(name, pagingSort);

        List<GetAllProductsResponse> responses = pagePro.getContent().stream()
                .map(product -> this.modelMapperService.forResponse()
                        .map(product, GetAllProductsResponse.class)).collect(Collectors.toList());

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("products", responses);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        logger.debug("Retrieved {} products for page {}. Total items: {}. Total pages: {}.", responses.size(), pagePro.getNumber(), pagePro.getTotalElements(), pagePro.getTotalPages());
        return response;
    }

    @Override
    public TreeMap<String, Object> getProductsByState(Boolean state, int page, int size, String[] sort) {
        logger.info("Fetching products by state '{}', with pagination. Page: {}, Size: {}, Sort: {}.", state, page, size, Arrays.toString(sort));
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        Page<Product> pagePro = this.productRepository.findByState(state, pagingSort);

        List<GetAllProductsResponse> responses = pagePro.getContent().stream()
                .map(product -> this.modelMapperService.forResponse()
                        .map(product, GetAllProductsResponse.class)).collect(Collectors.toList());

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("products", responses);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        logger.debug("Retrieved {} products for page {}. Total items: {}. Total pages: {}.", responses.size(), pagePro.getNumber(), pagePro.getTotalElements(), pagePro.getTotalPages());
        return response;
    }

    public TreeMap<String, Object> getProductsByInitialLetter(char initialLetter, int page, int size, String[] sort) {
        logger.info("Fetching products by initial letter '{}', with pagination. Page: {}, Size: {}, Sort: {}.", initialLetter, page, size, Arrays.toString(sort));
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(getOrder(sort)));
        Page<Product> pagePro = this.productRepository.findByInitialLetterIgnoreCase(initialLetter, pagingSort);

        List<GetAllProductsResponse> responses = pagePro.getContent().stream()
                .map(product -> this.modelMapperService.forResponse()
                        .map(product, GetAllProductsResponse.class)).collect(Collectors.toList());

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("products", responses);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        logger.debug("Retrieved {} products for page {}. Total items: {}. Total pages: {}.", responses.size(), pagePro.getNumber(), pagePro.getTotalElements(), pagePro.getTotalPages());
        return response;
    }

    @Override
    public GetAllProductsResponse getProductByBarcodeNumber(String barcodeNumber) {
        logger.info("Fetching product by barcode number '{}'.", barcodeNumber);
        Product product = this.productRepository.findByBarcodeNumber(barcodeNumber);
        if (product != null) {
            logger.debug("Product found with barcode number '{}'.", barcodeNumber);
            return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
        } else {
            logger.warn("No product found with barcode number '{}'.", barcodeNumber);
            throw new EntityNotFoundException("Product not found");
        }
    }

    @Override
    public GetAllProductsResponse getProductById(Long id) {
        logger.info("Fetching product by id '{}'.", id);
        Product product = this.productRepository.findById(id).orElseThrow(() -> {
            logger.warn("No product found with id '{}'.", id);
            return new EntityNotFoundException("Product not found");
        });
        logger.debug("Product found with id '{}'.", id);
        return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
    }

    @Override
    public GetAllProductsResponse addProduct(CreateProductRequest createProductRequest) {
        logger.info("Adding new product: '{}'.", createProductRequest.getName());
        Product existingProduct = this.productRepository.findByNameIgnoreCase(createProductRequest.getName());
        Product product = new Product();
        if (existingProduct != null) {
            double existingUnitPrice = existingProduct.getUnitPrice();
            double requestUnitPrice = createProductRequest.getUnitPrice();
            double epsilon = 0.0001;

            if (Math.abs(existingUnitPrice - requestUnitPrice) < epsilon) {
                product.setQuantity(existingProduct.getQuantity() + createProductRequest.getQuantity());
                product.setBarcodeNumber(existingProduct.getBarcodeNumber());
                this.productRepository.deleteById(existingProduct.getId());
                logger.debug("Existing product found with name '{}'. Updating quantity and deleting old product.", createProductRequest.getName());
            } else {
                logger.warn("Product with name '{}' already exists with different unit price.", createProductRequest.getName());
                throw new EntityAlreadyExistsException("Product already exists");
            }
        } else {
            product.setBarcodeNumber(UUID.randomUUID().toString().substring(0, 8));
            product.setQuantity(createProductRequest.getQuantity());
            logger.debug("Creating new product with name '{}'.", createProductRequest.getName());
        }
        product.setName(createProductRequest.getName());
        product.setDescription(createProductRequest.getDescription());
        product.setUnitPrice(createProductRequest.getUnitPrice());
        product.setState(createProductRequest.getState());
        product.setImageUrl(createProductRequest.getImageUrl());
        product.setCreatedBy(createProductRequest.getCreatedBy());
        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(createProductRequest.getProductCategoryId());
        product.setProductCategory(productCategory);
        product.setUpdatedAt(LocalDateTime.now());
        this.productRepository.save(product);
        logger.info("New product '{}' added successfully.", createProductRequest.getName());
        return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
    }

    @Override
    public GetAllProductsResponse updateProduct(UpdateProductRequest updateProductRequest) {
        logger.info("Updating product with id '{}'.", updateProductRequest.getId());
        Product existingProduct = this.productRepository.findById(updateProductRequest.getId()).orElseThrow(() -> {
            logger.warn("No product found with id '{}'.", updateProductRequest.getId());
            return new EntityNotFoundException("Product not found");
        });
        Product product = this.modelMapperService.forRequest().map(updateProductRequest, Product.class);
        this.productBusinessRules.checkUpdate(product, existingProduct);
        product.setUpdatedAt(LocalDateTime.now());
        this.productRepository.save(product);
        logger.debug("Product with id '{}' updated successfully.", updateProductRequest.getId());
        return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
    }

    @Override
    public GetAllProductsResponse deleteProduct(Long id) {
        logger.info("Deleting product with id '{}'.", id);
        Product product = this.productRepository.findById(id).orElseThrow(() -> {
            logger.warn("No product found with id '{}'.", id);
            return new EntityNotFoundException("Product not found");
        });
        this.productRepository.deleteById(id);
        logger.debug("Product with id '{}' deleted successfully.", id);
        return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
    }
}
