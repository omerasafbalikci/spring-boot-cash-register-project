package com.toyota.authenticationauthorizationservice.dao;

import com.toyota.authenticationauthorizationservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing users in database.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndDeletedIsFalse(String username);
    Optional<User> findByResetToken(String token);
    boolean existsByUsernameAndDeletedIsFalse(String username);
}
