package com.toyota.authenticationauthorizationservice.service.concretes;

import com.toyota.authenticationauthorizationservice.dao.RoleRepository;
import com.toyota.authenticationauthorizationservice.dao.TokenRepository;
import com.toyota.authenticationauthorizationservice.dao.UserRepository;
import com.toyota.authenticationauthorizationservice.domain.Role;
import com.toyota.authenticationauthorizationservice.domain.Token;
import com.toyota.authenticationauthorizationservice.domain.User;
import com.toyota.authenticationauthorizationservice.dto.requests.AuthenticationRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.PasswordRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.RegisterRequest;
import com.toyota.authenticationauthorizationservice.dto.responses.AuthenticationResponse;
import com.toyota.authenticationauthorizationservice.service.abstracts.JwtService;
import com.toyota.authenticationauthorizationservice.utilities.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserManagerTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private TokenRepository tokenRepository;
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        userManager = new UserManager(userRepository, authenticationManager, jwtService, passwordEncoder, roleRepository, tokenRepository, 3600000);
    }

    @Test
    void register_success() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("Ahmet", "test", Set.of(""));
        Role existingRole = new Role();

        // When
        when(userRepository.existsByUsernameAndDeletedIsFalse(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("...");
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(existingRole));
        when(userRepository.save(any(User.class))).thenAnswer(
                invocationOnMock -> invocationOnMock.getArgument(0)
        );
        boolean success = userManager.register(registerRequest);

        // Then
        assertTrue(success);
    }

    @Test
    void register_userAlreadyExists() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("Ahmet", "test", Set.of(""));

        // When
        when(userRepository.existsByUsernameAndDeletedIsFalse(anyString())).thenReturn(true);

        // Then
        assertThrows(UsernameTakenException.class, () -> userManager.register(registerRequest));
    }

    @Test
    void register_noRolesEntered() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("Ahmet", "test", Collections.emptySet());

        // Then
        assertThrows(NoRolesException.class, () -> userManager.register(registerRequest));
    }

    @Test
    void register_noValidRolesEntered() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("Ahmet", "test", Set.of(""));

        // When
        when(passwordEncoder.encode(anyString())).thenReturn("...");
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(NoRolesException.class, () -> userManager.register(registerRequest));
    }

    @Test
    void login_success() {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("tim", "test");
        User user = new User(1L, "tim", "test", false, Set.of(new Role()), List.of(new Token()));

        // When
        when(userRepository.findByUsernameAndDeletedIsFalse(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(tokenRepository.findAllValidTokensByUser(any())).thenReturn(List.of(new Token()));
        AuthenticationResponse response = userManager.login(authenticationRequest);

        // Then
        assertNotNull(response.getToken());
    }

    @Test
    void login_fail() {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("tim", "test");
        AuthenticationException authenticationException = new AuthenticationException("Failed Authentication") {};

        // When
        when(authenticationManager.authenticate(any())).thenThrow(authenticationException);

        // Then
        assertThrows(InvalidAuthenticationException.class, () -> userManager.login(authenticationRequest));
    }

    @Test
    void deleteUsername_success() {
        // Given
        String username = "username";
        User user = new User(1L, "username", "password", false, null, List.of(new Token()));

        // When
        when(userRepository.findByUsernameAndDeletedIsFalse(username)).thenReturn(Optional.of(user));
        Boolean result = userManager.deleteUsername(username);

        // Then
        assertTrue(user.isDeleted());
        assertTrue(result);
    }

    @Test
    void deleteUsername_userNotFound() {
        // Given
        String username = "username";

        // When
        when(userRepository.findByUsernameAndDeletedIsFalse(username)).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> userManager.deleteUsername(username));
    }

    @Test
    void updateUsername_success() {
        // Given
        User user = new User(1L, "username", "password", false, null, List.of(new Token()));
        String username = "username";
        String newUsername = "updatedUser";

        // When
        when(userRepository.findByUsernameAndDeletedIsFalse(username)).thenReturn(Optional.of(user));
        Boolean success = userManager.updateUsername(newUsername, username);

        // Then
        assertTrue(success);
        assertEquals(newUsername, user.getUsername());
    }

    @Test
    void updateUsername_userNotFound() {
        // Given
        String username = "username";
        String newUsername = "updatedUser";

        // When
        when(userRepository.findByUsernameAndDeletedIsFalse(username)).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> userManager.updateUsername(newUsername, username));
    }

    @Test
    void updateUsername_usernameTaken() {
        // Given
        String username = "username";
        String newUsername = "updatedUser";

        // When
        when(userRepository.existsByUsernameAndDeletedIsFalse(anyString())).thenReturn(true);

        // Then
        assertThrows(UsernameTakenException.class, () -> userManager.updateUsername(newUsername, username));
    }


    @Test
    void changePassword_success() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User(1L, "username", "password", false, null, List.of(new Token()));
        String username = "username";
        PasswordRequest passwordRequest = new PasswordRequest("password", "newPassword");

        // When
        when(request.getHeader("Authorization")).thenReturn("Bearer Token");
        when(jwtService.extractUsername(anyString())).thenReturn("username");
        when(userRepository.findByUsernameAndDeletedIsFalse(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newPassword");
        boolean success = userManager.changePassword(request, passwordRequest);

        // Then
        assertTrue(success);
        assertEquals(passwordRequest.getNewPassword(), user.getPassword());
    }

    @Test
    void changePassword_falsePassword() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User(1L, "username", "password", false, null, List.of(new Token()));
        String username = "username";
        PasswordRequest passwordRequest = new PasswordRequest("passwords", "newPassword");

        // When
        when(request.getHeader("Authorization")).thenReturn("Bearer Token");
        when(jwtService.extractUsername(anyString())).thenReturn("username");
        when(userRepository.findByUsernameAndDeletedIsFalse(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Then
        assertThrows(IncorrectPasswordException.class, () -> userManager.changePassword(request, passwordRequest));
    }

    @Test
    void changePassword_userNotFound() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        String username = "username";
        PasswordRequest passwordRequest = new PasswordRequest("passwords", "newPassword");

        // When
        when(request.getHeader("Authorization")).thenReturn("Bearer Token");
        when(jwtService.extractUsername(anyString())).thenReturn("username");
        when(userRepository.findByUsernameAndDeletedIsFalse(username)).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> userManager.changePassword(request, passwordRequest));
    }

    @Test
    void changePassword_bearerFail() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        PasswordRequest passwordRequest = new PasswordRequest("passwords", "newPassword");

        // When
        when(request.getHeader("Authorization")).thenReturn("");
        boolean success = userManager.changePassword(request, passwordRequest);

        // Then
        assertFalse(success);
    }

    @Test
    void verify() {
        // Given
        String username = "username";
        MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // When
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenReturn(new ArrayList<>());
        Map<String, String> result = userManager.verify();

        // Then
        assertTrue(result.containsKey("Username"));
        assertEquals(username, result.get("Username"));
        assertEquals(1, result.size());
        securityContextHolder.close();
    }

    @Test
    void addRole_success() {
        // Given
        Set<Role> roles = new HashSet<>();
        roles.add(new Role());
        User user = new User(1L, "username", "password", false, roles, List.of(new Token()));
        Role role = new Role(1L, "Admin", "", null);
        String username = "username";
        String roleStr = "Admin";

        // When
        when(userRepository.findByUsernameAndDeletedIsFalse(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        boolean success = userManager.addRole(username, roleStr);

        // Then
        assertTrue(success);
        assertEquals(2, user.getRoles().size());
    }

    @Test
    void addRole_userNotFound() {
        // Given
        String username = "username";
        String roleStr = "Admin";

        // Then
        assertThrows(UserNotFoundException.class, () -> userManager.addRole(username, roleStr));
    }

    @Test
    void addRole_roleNotFound() {
        // Given
        Set<Role> roles = new HashSet<>();
        roles.add(new Role());
        User user = new User(1L, "username", "password", false, roles, List.of(new Token()));
        String username = "username";
        String roleStr = "Admin";

        // When
        when(userRepository.findByUsernameAndDeletedIsFalse(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(RoleNotFoundException.class, () -> userManager.addRole(username, roleStr));
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void removeRole_success() {
        // Given
        Set<Role> roles = new HashSet<>();
        Role role = new Role(1L, "Admin", "", null);
        roles.add(new Role());
        roles.add(role);
        User user = new User(1L, "username", "password", false, roles, List.of(new Token()));
        String username = "username";
        String roleStr = "Admin";

        // When
        when(userRepository.findByUsernameAndDeletedIsFalse(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        boolean success = userManager.removeRole(username, roleStr);

        // Then
        assertTrue(success);
        assertEquals(1, user.getRoles().size());
        assertFalse(user.getRoles().contains(role));
    }

    @Test
    void removeRole_userNotFound() {
        // Given
        String username = "username";
        String roleStr = "Admin";

        // When
        when(userRepository.findByUsernameAndDeletedIsFalse(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> userManager.removeRole(username, roleStr));
    }

    @Test
    void removeRole_roleNotFound() {
        // Given
        Set<Role> roles = new HashSet<>();
        roles.add(new Role());
        User user = new User(1L, "username", "password", false, roles, List.of(new Token()));
        String username = "username";
        String roleStr = "Admin";

        // When
        when(userRepository.findByUsernameAndDeletedIsFalse(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(RoleNotFoundException.class, () -> userManager.removeRole(username, roleStr));
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void logout() {
        // Given
        String jwtToken = "Bearer token";
        User user = new User();
        Date currentDate = new Date();
        Token token = new Token("id", false, new Date(currentDate.getTime() + 60000), user);

        // When
        when(jwtService.extractTokenId(anyString())).thenReturn("jti");
        when(tokenRepository.findById(anyString())).thenReturn(Optional.of(token));
        userManager.logout(jwtToken);

        // Then
        assertTrue(token.getExpirationDate().after(new Date()));
        assertTrue(token.isRevoked());
    }

    @Test
    void logout_noUserForProvidedToken() {
        // Given
        String jwtToken = "Bearer token";

        // When
        when(jwtService.extractTokenId(anyString())).thenReturn("jti");
        when(tokenRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> userManager.logout(jwtToken));
    }

    @Test
    void logout_invalidBearer() {
        // Given
        String jwtToken = "";

        // Then
        assertThrows(InvalidBearerToken.class, () -> userManager.logout(jwtToken));
    }
}
