package com.toyota.salesservice.dao;

import com.toyota.salesservice.domain.Campaign;
import com.toyota.salesservice.domain.CampaignType;
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
 * Specification class for filtering campaigns based on various criteria.
 */

@RequiredArgsConstructor
public class CampaignSpecification implements Specification<Campaign> {
    private final Long id;
    private final String campaignNumber;
    private final String name;
    private final String campaignCategory;
    private final String campaignKey;
    private final Boolean state;
    private final String createdBy;

    /**
     * Converts the filter criteria into a {@link Predicate} that can be used in a query.
     *
     * @param root            the root
     * @param query           the criteria query
     * @param criteriaBuilder the criteria builder
     * @return a {@link Predicate} representing the filtering conditions
     */
    @Override
    public Predicate toPredicate(@NonNull Root<Campaign> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (id != null) {
            predicates.add(criteriaBuilder.equal(root.get("id"), id));
        }
        if (campaignNumber != null && !campaignNumber.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("campaignNumber"), "%" + campaignNumber + "%"));
        }
        if (name != null && !name.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
        }
        if (campaignCategory != null && !campaignCategory.isEmpty()) {
            CampaignType campaignType = CampaignType.valueOf(campaignCategory);
            predicates.add(criteriaBuilder.equal(root.get("campaignCategory"), campaignType));
        }
        if (campaignKey != null && !campaignKey.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("campaignKey"), "%" + campaignKey + "%"));
        }
        if (state != null) {
            predicates.add(criteriaBuilder.equal(root.get("state"), state));
        }
        if (createdBy != null && !createdBy.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("createdBy"), "%" + createdBy + "%"));
        }

        predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
