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
import com.toyota.authenticationauthorizationservice.dto.responses.UserManagementResponse;
import com.toyota.authenticationauthorizationservice.service.abstracts.JwtService;
import com.toyota.authenticationauthorizationservice.service.abstracts.MailService;
import com.toyota.authenticationauthorizationservice.service.abstracts.UserService;
import com.toyota.authenticationauthorizationservice.utilities.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing users.
 */

@Service
@AllArgsConstructor
public class UserManager implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final WebClient.Builder webClientBuilder;
    private final MailService mailService;
    private final Logger logger = LogManager.getLogger(UserService.class);
    @Value("${application.security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * Registers a new user with the given registration request.
     *
     * @param request the registration request containing the user details
     * @return true if the registration is successful, false otherwise
     * @throws UsernameTakenException if the username is already taken
     * @throws NoRolesException if no roles are provided for the registration
     */
    @Override
    public Boolean register(RegisterRequest request) {
        logger.info("Attempting to register user: {}", request.getUsername());
        if (this.userRepository.existsByUsernameAndDeletedIsFalse(request.getUsername())) {
            logger.warn("Username is already taken: {}", request.getUsername());
            throw new UsernameTakenException("Username is already taken! Username: " + request.getUsername());
        }
        if (request.getRoles().isEmpty()) {
            logger.warn("No roles found for registration for user: {}", request.getUsername());
            throw new NoRolesException("No role found for registration!");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        Set<Role> roles = new HashSet<>();
        user.setRoles(roles);
        for (String role : request.getRoles()) {
            Optional<Role> roleOptional = this.roleRepository.findByName(role);
            roleOptional.ifPresent(roles::add);
        }
        if (roles.isEmpty()) {
            logger.warn("No valid roles found for user: {}", request.getUsername());
            throw new NoRolesException("No valid roles found");
        }
        this.userRepository.save(user);
        logger.info("User registered successfully: {}", request.getUsername());
        return true;
    }

    /**
     * Authenticates a user with the given authentication request.
     *
     * @param request the authentication request containing the username and password
     * @return an authentication response containing the JWT token if authentication is successful
     * @throws InvalidAuthenticationException if the authentication fails
     */
    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        logger.info("Attempting to authenticate user: {}", request.getUsername());
        try {
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {}", request.getUsername());
            throw new InvalidAuthenticationException("Authentication failed! Username or password is incorrect");
        }
        User user = this.userRepository.findByUsernameAndDeletedIsFalse(request.getUsername()).orElseThrow();
        var jwt = this.jwtService.generateToken(user);
        revokeUserTokens(user);
        saveUserToken(user, jwt);
        logger.info("User authenticated successfully: {}", request.getUsername());
        return AuthenticationResponse.builder().token(jwt).build();
    }

    /**
     * Saves the given JWT token for the specified user.
     *
     * @param user the user for whom the token is to be saved
     * @param jwt the JWT token to be saved
     */
    private void saveUserToken(User user, String jwt) {
        String tokenId = this.jwtService.extractTokenId(jwt);
        Date currentDate = new Date();
        Token token = Token.builder()
                .tokenId(tokenId)
                .user(user)
                .expirationDate(new Date(currentDate.getTime() + jwtExpiration))
                .revoked(false)
                .build();
        this.tokenRepository.save(token);
        logger.info("Token saved for user: {}", user.getUsername());
    }

    /**
     * Revokes all valid tokens for the specified user.
     *
     * @param user the user for whom the tokens are to be revoked
     */
    private void revokeUserTokens(User user) {
        List<Token> tokens = this.tokenRepository.findAllValidTokensByUser(user.getId());
        tokens.forEach(token -> token.setRevoked(true));
        this.tokenRepository.saveAll(tokens);
        logger.info("All tokens revoked for user: {}", user.getUsername());
    }

    /**
     * Initiates a password reset process for the user with the given email.
     *
     * @param email the email address of the user requesting the password reset
     * @return true if the password reset process is successfully initiated, false otherwise
     * @throws UserNotFoundException if the user is not found either in the local database or the user management service
     * @throws UnexpectedException if an unexpected error occurs while communicating with the user management service
     */
    @Override
    public boolean initiatePasswordReset(String email) {
        logger.info("Initiating password reset for user with email: {}", email);
        UserManagementResponse userManagementResponse;
        try {
            userManagementResponse = this.webClientBuilder.build().get()
                    .uri("http://user-management-service/api/user-management/email", uriBuilder ->
                            uriBuilder.queryParam("email", email).build())
                    .retrieve()
                    .bodyToMono(UserManagementResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            logger.warn("User not found in user management service with email: {}", email);
            throw new UserNotFoundException("User not found in user management service with email: " + email);
        } catch (Exception e) {
            logger.warn("Error occurred while calling user management service: {}", e.getMessage());
            throw new UnexpectedException("Error occurred while calling user management service: " + e);
        }

        if (userManagementResponse != null && userManagementResponse.getUsername() != null) {
            Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(userManagementResponse.getUsername());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                String resetToken = generateResetToken();

                user.setResetToken(resetToken);
                user.setResetTokenExpiration(calculateResetTokenExpiration());
                this.userRepository.save(user);

                sendPasswordResetEmail(user.getUsername(), email, resetToken);

                logger.info("Password reset initiated successfully for user with email: {}", email);
                return true;
            } else {
                logger.warn("User not found in local database with email: {}", email);
                throw new UserNotFoundException("User not found in local database with email: " + email);
            }
        } else {
            logger.warn("User not found in user management service with email: {}", email);
            throw new UserNotFoundException("User not found in user management service with email: " + email);
        }
    }

    /**
     * Generates a unique token for password reset.
     *
     * @return a unique reset token
     */
    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Calculates the expiration date for the reset token.
     *
     * @return the expiration date of the reset token
     */
    private Date calculateResetTokenExpiration() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        return calendar.getTime();
    }

    /**
     * Sends a password reset email to the user.
     *
     * @param username the username of the user to whom the email is to be sent
     * @param userMail the email address of the user
     * @param resetToken the reset token to be included in the email
     */
    private void sendPasswordResetEmail(String username, String userMail, String resetToken) {
        String resetUrl = "http://localhost:8080/auth/reset-password?token=" + resetToken;
        String message = String.format("Hello %s,\n\nYou requested a password reset. Please use the following link to reset your password:\n%s\n\nIf you did not request this, please ignore this email.\n\nÖMER ASAF BALIKÇI", username, resetUrl);

        this.mailService.sendEmail(userMail, "Password Reset Request", message);
        logger.info("Password reset email sent to: {}", userMail);
    }

    /**
     * Handles the password reset process for the user with the given token.
     *
     * @param token the reset token
     * @param newPassword the new password to be set
     * @return a message indicating the result of the password reset process
     */
    @Override
    public String handlePasswordReset(String token, String newPassword) {
        logger.info("Handling password reset for token: {}", token);
        Optional<User> optionalUser = this.userRepository.findByResetTokenAndDeletedIsFalse(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(this.passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiration(null);
            this.userRepository.save(user);
            logger.info("Password reset successfully for token: {}", token);
            return "Password reset successfully.";
        } else {
            logger.warn("Invalid token: {}", token);
            return "Invalid token";
        }
    }

    /**
     * Logs out the user by revoking the specified JWT token.
     *
     * @param jwt the JWT token to be revoked
     * @throws InvalidBearerToken if the token is invalid or not provided
     * @throws UserNotFoundException if no user is found with the provided token
     */
    @Override
    public void logout(String jwt) {
        logger.info("Attempting to logout user with token: {}", jwt);
        if (jwt == null || !jwt.startsWith("Bearer ")) {
            logger.warn("Invalid bearer token provided for logout");
            throw new InvalidBearerToken("User has no bearer token or an invalid token");
        }
        String authHeader = jwt.substring(7);
        String tokenId = this.jwtService.extractTokenId(authHeader);
        Optional<Token> optionalToken = this.tokenRepository.findById(tokenId);
        if (optionalToken.isPresent()) {
            Token token = optionalToken.get();
            token.setRevoked(true);
            this.tokenRepository.save(token);
            logger.info("User logged out successfully");
        } else {
            logger.warn("No user found with the provided token for logout");
            throw new UserNotFoundException("No user found with the provided token");
        }
    }

    /**
     * Deletes the user with the specified username.
     *
     * @param username the username of the user to be deleted
     * @return true if the user is deleted successfully, false otherwise
     * @throws UserNotFoundException if no user is found with the provided username
     */
    @Override
    public Boolean deleteUsername(String username) {
        logger.info("Attempting to delete user: {}", username);
        Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setDeleted(true);
            this.userRepository.save(user);
            revokeUserTokens(user);
            logger.info("User deleted successfully: {}", username);
            return true;
        } else {
            logger.warn("Delete username: User not found: {}", username);
            throw new UserNotFoundException("User not found");
        }
     }

    /**
     * Updates the username of an existing user.
     *
     * @param newUsername the new username to be set
     * @param oldUsername the current username of the user
     * @return true if the username is updated successfully, false otherwise
     * @throws UsernameTakenException if the new username is already taken
     * @throws UserNotFoundException if no user is found with the current username
     */
    @Override
    public Boolean updateUsername(String newUsername, String oldUsername) {
        logger.info("Attempting to update username from {} to {}", oldUsername, newUsername);
        if (this.userRepository.existsByUsernameAndDeletedIsFalse(newUsername)) {
            logger.warn("New username is already taken: {}", newUsername);
            throw new UsernameTakenException("Username is already taken! Username: " + newUsername);
        }
        Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(oldUsername);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(newUsername);
            this.userRepository.save(user);
            revokeUserTokens(user);
            logger.info("Username updated successfully from {} to {}", oldUsername, newUsername);
            return true;
        } else {
            logger.warn("Old username not found: {}", oldUsername);
            throw new UserNotFoundException("Username not found");
        }
    }

    /**
     * Changes the password of the currently authenticated user.
     *
     * @param request the HTTP request containing the JWT token
     * @param passwordRequest the password request containing the old and new passwords
     * @return true if the password is changed successfully, false otherwise
     * @throws IncorrectPasswordException if the old password is incorrect
     * @throws UserNotFoundException if no user is found with the provided token
     */
    @Override
    public boolean changePassword(HttpServletRequest request, PasswordRequest passwordRequest) {
        logger.info("Attempting to change password for user");
        String authHeader = extractToken(request);
        if (authHeader == null) {
            logger.warn("No valid token found for password change");
            return false;
        }
        String username = this.jwtService.extractUsername(authHeader);

        Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (this.passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
                user.setPassword(this.passwordEncoder.encode(passwordRequest.getNewPassword()));
                this.userRepository.save(user);
                revokeUserTokens(user);
                logger.info("Password changed successfully for user: {}", username);
                return true;
            } else {
                logger.warn("Incorrect old password provided for user: {}", username);
                throw new IncorrectPasswordException("Password is incorrect");
            }
        } else {
            logger.warn("User not found for password change: {}", username);
            throw new UserNotFoundException("User not found! User may not be logged in");
        }
    }

    /**
     * Verifies the current authenticated user.
     *
     * @return a map containing the user's roles and username
     */
    @Override
    public Map<String, String> verify() {
        logger.info("Verifying current user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> map = authentication.getAuthorities().stream()
                .collect(Collectors.toMap(
                        GrantedAuthority::getAuthority,
                        GrantedAuthority::getAuthority
                ));
        map.put("Username", authentication.getName());
        return map;
    }

    /**
     * Adds the specified role to the user with the given username.
     *
     * @param username the username of the user to whom the role is to be added
     * @param role the role to be added
     * @return true if the role is added successfully, false otherwise
     * @throws RoleNotFoundException if the role is not found
     * @throws UserNotFoundException if no user is found with the provided username
     */
    @Override
    public boolean addRole(String username, String role) {
        logger.info("Attempting to add role {} to user {}", role, username);
        Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Role> optionalRole = this.roleRepository.findByName(role);
            if (optionalRole.isPresent()) {
                user.getRoles().add(optionalRole.get());
                this.userRepository.save(user);
                logger.info("Role {} added successfully to user {}", role, username);
                return true;
            } else {
                logger.warn("Add role: Role not found: {}", role);
                throw new RoleNotFoundException("Role not found");
            }
        }
        logger.warn("Add role: User not found: {}", username);
        throw new UserNotFoundException("User not found");
    }

    /**
     * Removes the specified role from the user with the given username.
     *
     * @param username the username of the user from whom the role is to be removed
     * @param role the role to be removed
     * @return true if the role is removed successfully, false otherwise
     * @throws RoleNotFoundException if the role is not found
     * @throws UserNotFoundException if no user is found with the provided username
     */
    @Override
    public boolean removeRole(String username, String role) {
        logger.info("Attempting to remove role {} from user {}", role, username);
        Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Role> optionalRole = this.roleRepository.findByName(role);
            if (optionalRole.isPresent()) {
                user.getRoles().remove(optionalRole.get());
                this.userRepository.save(user);
                logger.info("Role {} removed successfully from user {}", role, username);
                return true;
            } else {
                logger.warn("Remove role: Role not found: {}", role);
                throw new RoleNotFoundException("Role not found");
            }
        }
        logger.warn("Remove role: User not found: {}", username);
        throw new UserNotFoundException("User not found");
    }

    /**
     * Extracts the JWT token from the HTTP request.
     *
     * @param request the HTTP request
     * @return the extracted JWT token, or null if no valid token is found
     */
    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
