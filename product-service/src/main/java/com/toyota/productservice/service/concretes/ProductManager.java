package com.toyota.productservice.service.concretes;

import com.toyota.productservice.dao.ProductRepository;
import com.toyota.productservice.domain.Product;
import com.toyota.productservice.domain.ProductCategory;
import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.service.abstracts.ProductService;
import com.toyota.productservice.service.rules.ProductBusinessRules;
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
    public List<Product> getAllProducts(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return productRepository.findAll(Sort.by(orders));
    }

    @Override
    public TreeMap<String, Object> getAllProductsPage(String name, int page, int size, String[] sort) {
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
        if (name == null) {
            pagePro = productRepository.findAll(pagingSort);
        } else {
            pagePro = productRepository.findByNameContaining(name, pagingSort);
        }

        List<Product> products = pagePro.getContent();

        TreeMap<String, Object> response = new TreeMap<>();
        response.put("products", products);
        response.put("currentPage", pagePro.getNumber());
        response.put("totalItems", pagePro.getTotalElements());
        response.put("totalPages", pagePro.getTotalPages());
        return response;
    }

    @Override
    public GetAllProductsResponse getByIdProduct(Long id) {
        Product product = this.productRepository.findById(id).orElseThrow();

        GetAllProductsResponse response = this.modelMapperService.forResponse()
                .map(product, GetAllProductsResponse.class);
        return response;
    }

    @Override
    public void addProduct(CreateProductRequest createProductRequest) {
        this.productBusinessRules.checkIfProductNameExists(createProductRequest.getName());
        Product product = new Product();
        product.setName(createProductRequest.getName());
        product.setDescription(createProductRequest.getDescription());
        product.setQuantity(createProductRequest.getQuantity());
        product.setUnitPrice(createProductRequest.getUnitPrice());
        product.setImageUrl(createProductRequest.getImageUrl());
        product.setCreatedBy(createProductRequest.getCreatedBy());
        product.setUpdatedAt(createProductRequest.getUpdatedAt());

        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(createProductRequest.getProductCategoryId());
        product.setProductCategory(productCategory);
        this.productRepository.save(product);
    }

    @Override
    public void updateProduct(UpdateProductRequest updateProductRequest) {
        Product product = this.modelMapperService.forRequest().map(updateProductRequest, Product.class);
        this.productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        this.productRepository.deleteById(id);
    }
}
