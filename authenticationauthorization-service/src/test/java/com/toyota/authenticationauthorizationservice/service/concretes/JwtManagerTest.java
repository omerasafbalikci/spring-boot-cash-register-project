package com.toyota.authenticationauthorizationservice.service.concretes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class JwtManagerTest {
    @Mock
    private UserDetails userDetails;
    private JwtManager jwtManager;

    @BeforeEach
    void setUp() {
        jwtManager = new JwtManager("3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b", 3600000L);
    }

    @Test
    void extractUsername() {
        // Given
        String username = "testName";
        Mockito.when(userDetails.getUsername()).thenReturn(username);
        String token = jwtManager.generateToken(userDetails);

        // When
        String result = jwtManager.extractUsername(token);

        // Then
        assertEquals(username, result);
    }

    @Test
    void generateToken() {
        // Given
        String username = "testName";

        // When
        Mockito.when(userDetails.getUsername()).thenReturn(username);
        String token = jwtManager.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertTrue(jwtManager.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid() {
        // Given
        String username = "testUsername";
        Mockito.when(userDetails.getUsername()).thenReturn(username);
        String token = jwtManager.generateToken(userDetails);

        // When
        boolean valid = jwtManager.isTokenValid(token, userDetails);

        // Then
        assertTrue(valid);
    }

    @Test
    void extractTokenId() {
        // Given
        MockedStatic<UUID> mockedStatic = Mockito.mockStatic(UUID.class);
        String jti = "1234a";
        UUID uuid = mock(UUID.class);
        Mockito.when(UUID.randomUUID()).thenReturn(uuid);
        Mockito.when(uuid.toString()).thenReturn(jti);
        String token = jwtManager.generateToken(userDetails);

        // When
        String result = jwtManager.extractTokenId(token);

        // Then
        assertEquals(jti, result);
        mockedStatic.close();
    }
}
