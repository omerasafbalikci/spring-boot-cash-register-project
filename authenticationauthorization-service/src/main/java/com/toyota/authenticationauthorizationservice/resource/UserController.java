package com.toyota.authenticationauthorizationservice.resource;

import com.toyota.authenticationauthorizationservice.dto.requests.AuthenticationRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.PasswordRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.RegisterRequest;
import com.toyota.authenticationauthorizationservice.dto.responses.AuthenticationResponse;
import com.toyota.authenticationauthorizationservice.service.abstracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.Map;

/**
 * REST controller for managing authentication and authorization.
 */

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Registers a new user.
     *
     * @param request the registration request containing user details
     * @return true if registration is successful, false otherwise
     */
    @PostMapping("/signup")
    public Boolean register(@RequestBody RegisterRequest request) {
        return this.userService.register(request);
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request the authentication request containing username and password
     * @return the authentication response containing the JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        AuthenticationResponse response = this.userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Initiates a password reset process for the user with the given email.
     *
     * @param email the email address of the user requesting a password reset
     * @return a response entity with a message indicating the result of the password reset initiation
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") @Email(message = "It must be a valid email") String email) {
        boolean result = this.userService.initiatePasswordReset(email);
        if (result) {
            return ResponseEntity.ok("Password reset initiated successfully. Check your email.");
        } else {
            return ResponseEntity.badRequest().body("Failed to initiate password reset.");
        }
    }

    /**
     * Resets the password for the user with the given reset token.
     *
     * @param token the reset token sent to the user's email
     * @param newPassword the new password to set for the user
     * @return a response entity with a message indicating the result of the password reset
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestParam("newPassword") String newPassword) {
        String result = this.userService.handlePasswordReset(token, newPassword);
        if ("Password reset successfully.".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Updates the username of an existing user.
     *
     * @param newUsername the new username to be set
     * @param oldUsername the current username of the user
     * @return true if the username is updated successfully, false otherwise
     */
    @PutMapping("update/{old-username}")
    public Boolean updateUsername(@RequestBody String newUsername, @PathVariable("old-username") String oldUsername) {
        return this.userService.updateUsername(newUsername, oldUsername);
    }

    /**
     * Logs out the user by revoking the provided JWT token.
     *
     * @param token the JWT token to be revoked
     * @return a ResponseEntity indicating the result of the logout operation
     */
    @PostMapping("/logout")
    public ResponseEntity<Entity> logout(@RequestHeader("Authorization") String token) {
        this.userService.logout(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Adds a role to the user with the given username.
     *
     * @param username the username of the user
     * @param role the role to be added
     * @return true if the role is added successfully, false otherwise
     */
    @PutMapping("/add-role/{username}")
    public boolean addRole(@PathVariable("username") String username, @RequestBody String role) {
        return this.userService.addRole(username, role);
    }

    /**
     * Removes a role from the user with the given username.
     *
     * @param username the username of the user
     * @param role the role to be removed
     * @return true if the role is removed successfully, false otherwise
     */
    @PutMapping("/remove-role/{username}")
    public boolean removeRole(@PathVariable("username") String username, @RequestBody String role) {
        return this.userService.removeRole(username, role);
    }

    /**
     * Changes the password of the currently authenticated user.
     *
     * @param request the HTTP request containing the JWT token
     * @param passwordRequest the password request containing old and new passwords
     * @return a ResponseEntity indicating the result of the password change operation
     */
    @PutMapping("/change-password")
    public ResponseEntity<Entity> changePassword(HttpServletRequest request, @RequestBody @Valid PasswordRequest passwordRequest) {
        boolean success = this.userService.changePassword(request, passwordRequest);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Verifies the current authenticated user.
     *
     * @return a map containing the user's roles and username
     */
    @GetMapping("/verify")
    public Map<String, String> verify() {
        return this.userService.verify();
    }

    /**
     * Deletes the user with the given username.
     *
     * @param username the username of the user to be deleted
     * @return true if the user is deleted successfully, false otherwise
     */
    @PutMapping("/delete")
    public Boolean deleteUsername(@RequestBody String username) {
        return this.userService.deleteUsername(username);
    }
}
