package com.toyota.usermanagementservice.dao;

import com.toyota.usermanagementservice.domain.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Component for creating specifications to filter user entities based on various criteria.
 */

@Component
public class UserSpecification {
    /**
     * Creates a specification to filter User entities based on provided criteria.
     *
     * @param id        the ID of the user
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param username  the username of the user
     * @param email     the email of the user
     * @param gender    the gender of the user
     * @return a specification used to filter users
     */
    public static Specification<User> filterByCriteria(Long id, String firstName, String lastName, String username, String email, String gender) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (id != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), id));
            }
            if (firstName != null && !firstName.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("firstName"), firstName));
            }
            if (lastName != null && !lastName.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("lastName"), lastName));
            }
            if (username != null && !username.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("username"), username));
            }
            if (email != null && !email.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("email"), email));
            }
            if (gender != null && !gender.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("gender"), gender));
            }

            predicate = criteriaBuilder.and(predicate, criteriaBuilder.isFalse(root.get("deleted")));

            return predicate;
        };
    }
}
