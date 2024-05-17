package com.toyota.authenticationauthorizationservice.dao;

import com.toyota.authenticationauthorizationservice.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String user);
}
