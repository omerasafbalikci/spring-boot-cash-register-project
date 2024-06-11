package com.toyota.authenticationauthorizationservice.resource;

import com.toyota.authenticationauthorizationservice.dto.requests.AuthenticationRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.PasswordRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.RegisterRequest;
import com.toyota.authenticationauthorizationservice.dto.responses.AuthenticationResponse;
import com.toyota.authenticationauthorizationservice.service.concretes.UserManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.swing.text.html.parser.Entity;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserManager userManager;
    @InjectMocks
    private UserController userController;

    @Test
    void register() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();

        // When
        when(userManager.register(any(RegisterRequest.class))).thenReturn(true);
        boolean result = userController.register(registerRequest);

        // Then
        assertTrue(result);
    }

    @Test
    void login() {
        // Given
        AuthenticationRequest request = new AuthenticationRequest();
        AuthenticationResponse expected = new AuthenticationResponse("Token");

        // When
        when(userManager.login(any(AuthenticationRequest.class))).thenReturn(expected);
        ResponseEntity<AuthenticationResponse> response = userController.login(request);

        // Then
        assertEquals(expected, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateUsername() {
        // Given
        String newUsername = "new";
        String oldUsername = "old";

        // When
        when(userManager.updateUsername(anyString(), anyString())).thenReturn(true);
        boolean success = userController.updateUsername(newUsername, oldUsername);

        // Then
        assertTrue(success);
    }

    @Test
    void addRole() {
        // Given
        String username = "username";
        String role = "role";

        // When
        when(userManager.addRole(anyString(), anyString())).thenReturn(true);
        boolean success = userController.addRole(username, role);

        // Then
        assertTrue(success);
    }

    @Test
    void removeRole() {
        // Given
        String username = "username";
        String role = "role";

        // When
        when(userManager.removeRole(anyString(), anyString())).thenReturn(true);
        boolean success = userController.removeRole(username, role);

        // Then
        assertTrue(success);
    }

    @Test
    void changePassword_success() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        PasswordRequest passwordRequest = new PasswordRequest();

        // When
        when(userManager.changePassword(any(), any())).thenReturn(true);
        ResponseEntity<Entity> response = userController.changePassword(request, passwordRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changePassword_fail() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        PasswordRequest passwordRequest = new PasswordRequest();

        // When
        when(userManager.changePassword(any(), any())).thenReturn(false);
        ResponseEntity<Entity> response = userController.changePassword(request, passwordRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void verify() {
        // Given
        Map<String, String> map = new HashMap<>();

        // When
        when(userManager.verify()).thenReturn(map);
        Map<String, String> result = userController.verify();

        // Then
        Mockito.verify(userManager).verify();
        assertEquals(map, result);
    }

    @Test
    void deleteUsername() {
        // Given
        String username = "username";

        // When
        when(userManager.deleteUsername(anyString())).thenReturn(true);
        boolean result = userController.deleteUsername(username);

        // Then
        assertTrue(result);
    }

    @Test
    void logout() {
        // Given
        String token = "Bearer token";

        // When
        ResponseEntity<Entity> response = userController.logout(token);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
