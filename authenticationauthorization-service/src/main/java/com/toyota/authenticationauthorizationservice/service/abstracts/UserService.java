package com.toyota.authenticationauthorizationservice.service.abstracts;

import com.toyota.authenticationauthorizationservice.dto.requests.AuthenticationRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.PasswordRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.RegisterRequest;
import com.toyota.authenticationauthorizationservice.dto.responses.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface UserService {
    Boolean register(RegisterRequest request);
    AuthenticationResponse login(AuthenticationRequest request);
    void logout(String jwt);
    Boolean deleteUsername(String username);
    Boolean updateUsername(String newUsername, String oldUsername);
    boolean changePassword(HttpServletRequest request, PasswordRequest passwordRequest);
    Map<String, String> verify();
    boolean addRole(String username, String role);
    boolean removeRole(String username, String role);
}
