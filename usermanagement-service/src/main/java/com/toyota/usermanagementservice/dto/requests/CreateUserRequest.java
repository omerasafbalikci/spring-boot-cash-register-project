package com.toyota.usermanagementservice.dto.requests;

import com.toyota.usermanagementservice.domain.Gender;
import com.toyota.usermanagementservice.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NotNull(message = "First name must not be null")
    @NotBlank(message = "First name must not be blank")
    private String firstName;
    @NotNull(message = "Last name must not be null")
    @NotBlank(message = "Last name must not be blank")
    private String lastName;
    @NotNull(message = "username must not be null")
    @NotBlank(message = "username must not be blank")
    private String username;
    @Email(message = "It must be a valid email")
    @NotNull(message = "Email must not be null")
    @NotBlank(message = "Email must not be blank")
    private String email;
    @NotNull(message = "Password must not be null")
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8,message = "Password must have at least 8 characters")
    private String password;
    @Size(min = 1,message = "User must have at least one role")
    @NotNull(message = "Role must not be null")
    private Set<Role> roles;
    private Gender gender;
}
