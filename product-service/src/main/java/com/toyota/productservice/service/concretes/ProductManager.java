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
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class ProductManager implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapperService modelMapperService;
    @Autowired
    private ProductBusinessRules productBusinessRules;

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    @Override
    public TreeMap<String, Object> getAllProductsPage(int page, int size, String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }

        Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
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
        return response;
    }

    @Override
    public TreeMap<String, Object> getProductsByNameContaining(String name, int page, int size, String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }

        Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
        Page<Product> pagePro;

        pagePro = this.productRepository.findByNameContainingIgnoreCase(name, pagingSort);

        List<GetAllProductsResponse> responses = pagePro.getContent().stream()
                .map(product -> this.modelMapperService.forResponse()
                        .map(product, GetAllProductsResponse.class)).collect(Collectors.toList());

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("products", responses);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        return response;
    }

    @Override
    public TreeMap<String, Object> getProductsByState(Boolean state, int page, int size, String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }

        Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
        Page<Product> pagePro = this.productRepository.findByState(state, pagingSort);

        List<GetAllProductsResponse> responses = pagePro.getContent().stream()
                .map(product -> this.modelMapperService.forResponse()
                        .map(product, GetAllProductsResponse.class)).collect(Collectors.toList());

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("products", responses);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        return response;
    }

    public TreeMap<String, Object> getProductsByInitialLetter(char initialLetter, int page, int size, String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }

        Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
        Page<Product> pagePro = this.productRepository.findByInitialLetterIgnoreCase(initialLetter, pagingSort);

        List<GetAllProductsResponse> responses = pagePro.getContent().stream()
                .map(product -> this.modelMapperService.forResponse()
                        .map(product, GetAllProductsResponse.class)).collect(Collectors.toList());

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("products", responses);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        return response;
    }

    @Override
    public GetAllProductsResponse getProductByBarcodeNumber(String barcodeNumber) {
        Product product = this.productRepository.findByBarcodeNumber(barcodeNumber);
        if (product != null) {
            return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
        } else {
            throw new EntityNotFoundException("Product not found");
        }
    }

    @Override
    public GetAllProductsResponse getProductById(Long id) {
        Product product = this.productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
    }

    @Override
    public GetAllProductsResponse addProduct(CreateProductRequest createProductRequest) {
        Product existingProduct = this.productRepository.findByNameIgnoreCase(createProductRequest.getName());
        Product product = new Product();
        if (existingProduct != null) {
            double existingUnitPrice = existingProduct.getUnitPrice();
            double requestUnitPrice = createProductRequest.getUnitPrice();
            double epsilon = 0.0001;

            if (Math.abs(existingUnitPrice - requestUnitPrice) < epsilon) {
                product.setQuantity(existingProduct.getQuantity() + createProductRequest.getQuantity());
                this.productRepository.deleteById(existingProduct.getId());
            } else {
                throw new EntityAlreadyExistsException("Product already exists");
            }
        } else {
            product.setBarcodeNumber(UUID.randomUUID().toString().substring(0, 8));
            product.setQuantity(createProductRequest.getQuantity());
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
        return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
    }

    @Override
    public GetAllProductsResponse updateProduct(UpdateProductRequest updateProductRequest) {
        Product existingProduct = this.productRepository.findById(updateProductRequest.getId()).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Product product = this.modelMapperService.forRequest().map(updateProductRequest, Product.class);
        this.productBusinessRules.checkUpdate(product, existingProduct);
        product.setUpdatedAt(LocalDateTime.now());
        this.productRepository.save(product);
        return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
    }

    @Override
    public GetAllProductsResponse deleteProduct(Long id) {
        Product product = this.productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        this.productRepository.deleteById(id);
        return this.modelMapperService.forResponse().map(product, GetAllProductsResponse.class);
    }
}
