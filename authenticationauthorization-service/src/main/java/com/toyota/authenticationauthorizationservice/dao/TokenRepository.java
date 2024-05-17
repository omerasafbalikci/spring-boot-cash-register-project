package com.toyota.authenticationauthorizationservice.dao;

import com.toyota.authenticationauthorizationservice.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findAllValidTokensByUser(Long userId);
}
