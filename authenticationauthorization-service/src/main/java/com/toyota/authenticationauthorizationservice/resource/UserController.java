package com.toyota.authenticationauthorizationservice.resource;

import com.toyota.authenticationauthorizationservice.dto.requests.AuthenticationRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.PasswordRequest;
import com.toyota.authenticationauthorizationservice.dto.requests.RegisterRequest;
import com.toyota.authenticationauthorizationservice.dto.responses.AuthenticationResponse;
import com.toyota.authenticationauthorizationservice.service.abstracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public Boolean register(@RequestBody RegisterRequest request) {
        return this.userService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        AuthenticationResponse response = this.userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("update/{oldUsername}")
    public Boolean update(@RequestBody String newUsername, @PathVariable("oldUsername") String oldUsername) {
        return this.userService.updateUsername(newUsername, oldUsername);
    }

    @PostMapping("/logout")
    public ResponseEntity<Entity> logout(@RequestHeader("Authorization") String token) {
        this.userService.logout(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/addRole/{username}")
    public boolean addRole(@PathVariable("username") String username, @RequestBody String role) {
        return this.userService.addRole(username, role);
    }

    @PutMapping("/removeRole/{username}")
    public boolean removeRole(@PathVariable("username") String username, @RequestBody String role) {
        return this.userService.removeRole(username, role);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Entity> changePassword(HttpServletRequest request, @RequestBody @Valid PasswordRequest passwordRequest) {
        boolean success = this.userService.changePassword(request, passwordRequest);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/verify")
    public Map<String, String> verify() {
        return this.userService.verify();
    }

    @PutMapping("/delete")
    public Boolean delete(@RequestBody String username) {
        return this.userService.delete(username);
    }
}
