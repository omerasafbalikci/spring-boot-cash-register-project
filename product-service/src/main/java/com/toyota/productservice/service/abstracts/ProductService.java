package com.toyota.productservice.service.abstracts;

import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.InventoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.dto.responses.InventoryResponse;

import java.util.List;
import java.util.TreeMap;

/**
 * Interface for product's service class.
 */

public interface ProductService {
    /**
     * Retrieves a filtered list of products based on the given parameters.
     *
     * @param page          the page number for pagination.
     * @param size          the number of items per page.
     * @param sort          the sorting criteria.
     * @param id            the ID of the product to filter by.
     * @param barcodeNumber the barcode number of the product to filter by.
     * @param state         the state of the product to filter by.
     * @return a {@link TreeMap} containing the filtered products and additional pagination information.
     */
    TreeMap<String, Object> getProductFiltered(int page, int size, String[] sort, Long id, String barcodeNumber, Boolean state);

    /**
     * Retrieves products whose names contain the specified string.
     *
     * @param name the string to search for in product names.
     * @param page the page number for pagination.
     * @param size the number of items per page.
     * @param sort the sorting criteria.
     * @return a {@link TreeMap} containing the products matching the search criteria and additional pagination information.
     */
    TreeMap<String, Object> getProductsByNameContaining(String name, int page, int size, String[] sort);

    /**
     * Checks the availability of products in the inventory.
     *
     * @param inventoryRequests a list of {@link InventoryRequest} objects representing the products to check.
     * @return a list of {@link InventoryResponse} objects representing the inventory status of the requested products.
     */
    List<InventoryResponse> checkProductInInventory(List<InventoryRequest> inventoryRequests);

    /**
     * Updates the quantity of products in inventory.
     *
     * @param inventoryRequests a list of {@link InventoryRequest} objects representing the products and quantities to update.
     */
    void updateProductInInventory(List<InventoryRequest> inventoryRequests);

    /**
     * Adds a new product.
     *
     * @param createProductRequest a {@link CreateProductRequest} object containing the details of the product to be added.
     * @return a {@link GetAllProductsResponse} representing the newly created product.
     */
    GetAllProductsResponse addProduct(CreateProductRequest createProductRequest);

    /**
     * Updates an existing product.
     *
     * @param updateProductRequest a {@link UpdateProductRequest} object containing the updated details of the product.
     * @return a {@link GetAllProductsResponse} representing the updated product.
     */
    GetAllProductsResponse updateProduct(UpdateProductRequest updateProductRequest);

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to be deleted.
     * @return a {@link GetAllProductsResponse} representing the deleted product.
     */
    GetAllProductsResponse deleteProduct(Long id);
}
