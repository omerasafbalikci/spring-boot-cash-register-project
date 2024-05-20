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
    private final Boolean state;

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
        if (state != null) {
            predicates.add(criteriaBuilder.equal(root.get("state"), state));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
