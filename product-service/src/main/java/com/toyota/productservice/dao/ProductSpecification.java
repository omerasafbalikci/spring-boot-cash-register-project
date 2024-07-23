package com.toyota.productservice.dao;

import com.toyota.productservice.domain.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for filtering products based on various criteria.
 */

@RequiredArgsConstructor
public class ProductSpecification implements Specification<Product> {
    private final Long id;
    private final String barcodeNumber;
    private final String name;
    private final Integer quantity;
    private final Double unitPrice;
    private final Boolean state;
    private final String createdBy;
    private final Long categoryId;

    /**
     * Constructs a {@link Predicate} based on the filtering criteria provided.
     *
     * @param root the root
     * @param query the criteria query
     * @param criteriaBuilder the criteria builder
     * @return a {@link Predicate} representing the filtering conditions
     */
    @Override
    public Predicate toPredicate(@NonNull Root<Product> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), id));
        }
        if (barcodeNumber != null && !barcodeNumber.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("barcodeNumber"), "%" + barcodeNumber + "%"));
        }
        if (name != null && !name.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
        }
        if (quantity != null) {
            predicates.add(criteriaBuilder.equal(root.get("quantity"), quantity));
        }
        if (unitPrice != null) {
            predicates.add(criteriaBuilder.equal(root.get("unitPrice"), unitPrice));
        }
        if (state != null) {
            predicates.add(criteriaBuilder.equal(root.get("state"), state));
        }
        if (createdBy != null && !createdBy.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("createdBy"), "%" + createdBy + "%"));
        }
        if (categoryId != null) {
            predicates.add(criteriaBuilder.equal(root.get("productCategory").get("id"), categoryId));
        }

        predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
