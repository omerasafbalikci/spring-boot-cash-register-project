package com.toyota.authenticationauthorizationservice.config;

import com.toyota.authenticationauthorizationservice.dao.RoleRepository;
import com.toyota.authenticationauthorizationservice.dao.UserRepository;
import com.toyota.authenticationauthorizationservice.domain.Role;
import com.toyota.authenticationauthorizationservice.dto.requests.RegisterRequest;
import com.toyota.authenticationauthorizationservice.service.abstracts.UserService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class DatabaseInitializer {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    @PostConstruct
    public void initialize() {
        List<Role> roles = this.roleRepository.findAll();
        if (roles.isEmpty()) {
            List<Role> roleList = List.of(
                    new Role(1L, "ADMIN", "ADMIN", null),
                    new Role(2L, "MANAGER", "MANAGER", null),
                    new Role(3L, "CASHIER", "CASHIER", null)
            );
            this.roleRepository.saveAll(roleList);
        }
        if (!this.userRepository.existsByUsernameAndDeletedIsFalse("admin")) {
            RegisterRequest request = new RegisterRequest("admin", "admin", Set.of("ADMIN", "MANAGER", "CASHIER"));
            this.userService.register(request);
        }
    }
}
