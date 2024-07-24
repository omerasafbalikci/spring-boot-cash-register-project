package com.toyota.productservice.dao;

import com.toyota.productservice.domain.ProductCategory;
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
 * Specification class for filtering product categories based on various criteria.
 */

@RequiredArgsConstructor
public class ProductCategorySpecification implements Specification<ProductCategory> {
    private final Long id;
    private final String categoryNumber;
    private final String name;
    private final String createdBy;

    /**
     * Converts the filter criteria into a {@link Predicate} that can be used in a query.
     *
     * @param root the root
     * @param query the criteria query
     * @param criteriaBuilder the criteria builder
     * @return a {@link Predicate} representing the filtering conditions
     */
    @Override
    public Predicate toPredicate(@NonNull Root<ProductCategory> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), id));
        }
        if (categoryNumber != null && !categoryNumber.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("categoryNumber"), "%" + categoryNumber + "%"));
        }
        if (name != null && !name.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
        }
        if (createdBy != null && !createdBy.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("createdBy"), "%" + createdBy + "%"));
        }

        predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
