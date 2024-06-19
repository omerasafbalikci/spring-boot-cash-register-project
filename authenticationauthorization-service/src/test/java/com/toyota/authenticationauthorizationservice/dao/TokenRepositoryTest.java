package com.toyota.authenticationauthorizationservice.dao;

import com.toyota.authenticationauthorizationservice.domain.Role;
import com.toyota.authenticationauthorizationservice.domain.Token;
import com.toyota.authenticationauthorizationservice.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class TokenRepositoryTest {
    @Mock
    private TokenRepository tokenRepository;

    @Test
    void testFindAllValidTokensByUser() {
        Role testRole = Role.builder()
                .name("test")
                .build();
        Set<Role> roles = new HashSet<>();
        roles.add(testRole);

        User testUser = User.builder()
                .id(1L)
                .username("test")
                .password("test")
                .deleted(false)
                .roles(roles)
                .build();

        Token validToken = Token.builder()
                .tokenId("valid-token")
                .user(testUser)
                .revoked(false)
                .expirationDate(new Date(System.currentTimeMillis() + 86400000))
                .build();
        Mockito.when(tokenRepository.findAllValidTokensByUser(testUser.getId())).thenReturn(Collections.singletonList(validToken));

        List<Token> tokens = tokenRepository.findAllValidTokensByUser(testUser.getId());

        Assertions.assertThat(tokens).hasSize(1);
        Assertions.assertThat(tokens.get(0).getTokenId()).isEqualTo("valid-token");
    }
}
