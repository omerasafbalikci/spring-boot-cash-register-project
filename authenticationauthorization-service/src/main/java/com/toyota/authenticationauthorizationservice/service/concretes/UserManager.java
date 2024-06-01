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

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserManager implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final Logger logger = LogManager.getLogger(UserService.class);
    @Value("${application.security.jwt.expiration-time}")
    private long jwtExpiration;

    @Override
    public Boolean register(RegisterRequest request) {
        if (this.userRepository.existsByUsernameAndDeletedIsFalse(request.getUsername())) {
            throw new UsernameTakenException("Username is already taken! Username: " + request.getUsername());
        }
        if (request.getRoles().isEmpty()) {
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
            throw new NoRolesException("No valid roles found");
        }
        this.userRepository.save(user);
        return true;
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new InvalidAuthenticationException("Authentication failed! Username or password is incorrect");
        }
        User user = this.userRepository.findByUsernameAndDeletedIsFalse(request.getUsername()).orElseThrow();
        var jwt = this.jwtService.generateToken(user);
        revokeUserTokens(user);
        saveUserToken(user, jwt);
        return AuthenticationResponse.builder().token(jwt).build();
    }

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
    }

    private void revokeUserTokens(User user) {
        List<Token> tokens = this.tokenRepository.findAllValidTokensByUser(user.getId());
        tokens.forEach(token -> token.setRevoked(true));
        this.tokenRepository.saveAll(tokens);
    }

    @Override
    public void logout(String jwt) {
        if (jwt == null || !jwt.startsWith("Bearer ")) {
            throw new InvalidBearerToken("User has no bearer token or an invalid token");
        }
        String authHeader = jwt.substring(7);
        String tokenId = this.jwtService.extractTokenId(authHeader);
        Optional<Token> optionalToken = this.tokenRepository.findById(tokenId);
        if (optionalToken.isPresent()) {
            Token token = optionalToken.get();
            token.setRevoked(true);
            this.tokenRepository.save(token);
        } else {
            throw new UserNotFoundException("No user found with the provided token");
        }
    }

    @Override
    public Boolean deleteUsername(String username) {
        Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setDeleted(true);
            this.userRepository.save(user);
            revokeUserTokens(user);
            return true;
        } else {
            throw new UserNotFoundException("User not found");
        }
     }

    @Override
    public Boolean updateUsername(String newUsername, String oldUsername) {
        if (this.userRepository.existsByUsernameAndDeletedIsFalse(newUsername)) {
            throw new UsernameTakenException("Username is already taken! Username: " + newUsername);
        }
        Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(oldUsername);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(newUsername);
            this.userRepository.save(user);
            revokeUserTokens(user);
            return true;
        } else {
            throw new UserNotFoundException("Username not found");
        }
    }

    @Override
    public boolean changePassword(HttpServletRequest request, PasswordRequest passwordRequest) {
        String authHeader = extractToken(request);
        if (authHeader == null) return false;
        String username = this.jwtService.extractUsername(authHeader);

        Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (this.passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
                user.setPassword(this.passwordEncoder.encode(passwordRequest.getNewPassword()));
                this.userRepository.save(user);
                revokeUserTokens(user);
                return true;
            } else {
                throw new IncorrectPasswordException("Password is incorrect");
            }
        } else {
            throw new UserNotFoundException("User not found! User may not be logged in");
        }
    }

    @Override
    public Map<String, String> verify() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> map = authentication.getAuthorities().stream()
                .collect(Collectors.toMap(
                        GrantedAuthority::getAuthority,
                        GrantedAuthority::getAuthority
                ));
        map.put("Username", authentication.getName());
        return map;
    }

    @Override
    public boolean addRole(String username, String role) {
        Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Role> optionalRole = this.roleRepository.findByName(role);
            if (optionalRole.isPresent()) {
                user.getRoles().add(optionalRole.get());
                this.userRepository.save(user);
                return true;
            } else {
                throw new RoleNotFoundException("Role not found");
            }
        }
        throw new UserNotFoundException("User not found");
    }

    @Override
    public boolean removeRole(String username, String role) {
        Optional<User> optionalUser = this.userRepository.findByUsernameAndDeletedIsFalse(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Role> optionalRole = this.roleRepository.findByName(role);
            if (optionalRole.isPresent()) {
                user.getRoles().remove(optionalRole.get());
                this.userRepository.save(user);
                return true;
            } else {
                throw new RoleNotFoundException("Role not found");
            }
        }
        throw new UserNotFoundException("User not found");
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
