package com.toyota.usermanagementservice.dao;

import com.toyota.usermanagementservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> getUsersFiltered(Long id, String firstName, String lastName, String username, String email, Pageable pageable);

    Boolean existsByUsernameAndDeletedIsFalse(String username);
    Boolean existsByEmailAndDeletedIsFalse(String email);
}
