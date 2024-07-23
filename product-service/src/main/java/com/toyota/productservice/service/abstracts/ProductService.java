package com.toyota.productservice.service.abstracts;

import com.toyota.productservice.dto.requests.CreateProductRequest;
import com.toyota.productservice.dto.requests.InventoryRequest;
import com.toyota.productservice.dto.requests.UpdateProductRequest;
import com.toyota.productservice.dto.responses.GetAllProductsResponse;
import com.toyota.productservice.dto.responses.InventoryResponse;

import java.util.List;
import java.util.Map;

/**
 * Interface for product's service class.
 */

public interface ProductService {
    /**
     * Retrieves filtered and paginated products.
     *
     * @param page          the page number to retrieve
     * @param size          the number of items per page
     * @param sort          the sorting criteria
     * @param id            the ID to filter by
     * @param barcodeNumber the barcode number to filter by
     * @param name          the name to filter by
     * @param quantity      the quantity to filter by
     * @param unitPrice     the unit price to filter by
     * @param state         the state to filter by
     * @param createdBy     the creator to filter by
     * @return a TreeMap containing the filtered products and pagination details
     */
    Map<String, Object> getProductsFiltered(int page, int size, String[] sort, Long id, String barcodeNumber, String name,
                                            Integer quantity, Double unitPrice, Boolean state, String createdBy);

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
     * Soft deletes a product by its ID (marks it as deleted without removing it from the database).
     *
     * @param id the ID of the product to be soft deleted
     * @return the details of the soft-deleted product
     */
    GetAllProductsResponse deleteProduct(Long id);
}
