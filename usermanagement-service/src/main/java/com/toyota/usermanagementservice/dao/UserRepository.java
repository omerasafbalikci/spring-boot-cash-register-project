package com.toyota.usermanagementservice.dao;

import com.toyota.usermanagementservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing user table in database.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByUsernameAndDeletedIsFalse(String username);
    boolean existsByEmailAndDeletedIsFalse(String email);
}
