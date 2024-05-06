package com.toyota.usermanagementservice.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotNull(message = "username must not be null")
    @NotBlank(message = "username must not be blank")
    private String username;
    @NotNull(message = "Password must not be null")
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8,message = "Password must have at least 8 characters")
    private String password;
    @Size(min = 1,message = "User must have at least one role")
    @NotNull(message = "Role must not be null")
    private Set<String> roles;
}
