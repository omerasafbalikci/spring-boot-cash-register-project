package com.toyota.salesservice.dao;

import com.toyota.salesservice.domain.Sales;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Component for creating specifications to filter Sales entities based on various criteria.
 */

@Component
public class SalesSpecification {
    /**
     * Creates a specification to filter Sales entities based on the provided criteria.
     *
     * @param id           the ID of the sale
     * @param salesNumber  the sales number of the sale
     * @param salesDate    the sales date of the sale
     * @param createdBy    the creator of the sale
     * @param paymentType  the payment type of the sale
     * @param totalPrice   the total price of the sale
     * @param money        the money on sale
     * @param change       the change on sale
     * @return a Specification object used to filter Sales entities
     */
    public static Specification<Sales> filterByCriteria(Long id, String salesNumber, String salesDate, String createdBy,
                                                        String paymentType, Double totalPrice, Double money, Double change) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (id != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), id));
            }
            if (salesNumber != null && !salesNumber.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("salesNumber"), salesNumber));
            }
            if (salesDate != null && !salesDate.isEmpty()) {
                List<DateTimeFormatter> formatters = Arrays.asList(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                );
                boolean parsed = false;
                for (DateTimeFormatter formatter : formatters) {
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(salesDate, formatter);
                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("salesDate"), dateTime));
                        parsed = true;
                        break;
                    } catch (DateTimeParseException ignored) {
                    }
                }
                if (!parsed) {
                    throw new IllegalArgumentException("Invalid date format: " + salesDate);
                }
            }
            if (createdBy != null && !createdBy.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("createdBy"), "%" + createdBy + "%"));
            }
            if (paymentType != null && !paymentType.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("paymentType"), paymentType));
            }
            if (totalPrice != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("totalPrice"), totalPrice));
            }
            if (money != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("money"), money));
            }
            if (change != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("change"), change));
            }

            predicate = criteriaBuilder.and(predicate, criteriaBuilder.isFalse(root.get("deleted")));

            return predicate;
        };
    }
}
