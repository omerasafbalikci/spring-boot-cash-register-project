package com.toyota.authenticationauthorizationservice.service.abstracts;

import com.toyota.authenticationauthorizationservice.dto.requests.AuthenticationRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.PasswordRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.RegisterRequest;
import com.toyota.authenticationauthorizationservice.dto.responses.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * Interface for user's service class.
 */

public interface UserService {
    /**
     * Registers a new user with the provided registration request details.
     *
     * @param request the registration request containing user details
     * @return true if the registration is successful, false otherwise
     */
    Boolean register(RegisterRequest request);

    /**
     * Authenticates a user based on the provided authentication request details.
     *
     * @param request the authentication request containing user credentials
     * @return an authentication response containing JWT token
     */
    AuthenticationResponse login(AuthenticationRequest request);

    /**
     * Initiates the password reset process for the user with the given email.
     *
     * @param email the email address of the user requesting the password reset
     * @return true if the password reset process is successfully initiated, false otherwise
     */
    boolean initiatePasswordReset(String email);

    /**
     * Handles the password reset process for the user with the given token.
     *
     * @param token the reset token
     * @param newPassword the new password to be set
     * @return a message indicating the result of the password reset process
     */
    String handlePasswordReset(String token, String newPassword);

    /**
     * Logs out a user by invalidating the provided JWT token.
     *
     * @param jwt the JWT token to invalidate
     */
    void logout(String jwt);

    /**
     * Deletes a user based on the provided username.
     *
     * @param username the username of the user to delete
     * @return true if the deletion is successful, false otherwise
     */
    Boolean deleteUsername(String username);

    /**
     * Updates the username of a user.
     *
     * @param newUsername the new username to set
     * @param oldUsername the old username to replace
     * @return true if the update is successful, false otherwise
     */
    Boolean updateUsername(String newUsername, String oldUsername);

    /**
     * Changes the password of a user based on the provided password request details.
     *
     * @param request the HTTP servlet request
     * @param passwordRequest the password request containing old and new password details
     * @return true if the password change is successful, false otherwise
     */
    boolean changePassword(HttpServletRequest request, PasswordRequest passwordRequest);

    /**
     * Verifies the current user based on the request context.
     *
     * @return a map containing verification details
     */
    Map<String, String> verify();

    /**
     * Adds a role to the user specified by the username.
     *
     * @param username the username of the user
     * @param role the role to add to the user
     * @return true if the role addition is successful, false otherwise
     */
    boolean addRole(String username, String role);

    /**
     * Removes a role from the user specified by the username.
     *
     * @param username the username of the user
     * @param role the role to remove from the user
     * @return true if the role removal is successful, false otherwise
     */
    boolean removeRole(String username, String role);
}
