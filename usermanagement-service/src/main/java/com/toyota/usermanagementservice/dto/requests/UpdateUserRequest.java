package com.toyota.usermanagementservice.dto.requests;

import com.toyota.usermanagementservice.domain.Gender;
import com.toyota.usermanagementservice.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO to update user.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotNull(message = "Id must not be null")
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    @Email(message = "It must be a valid email")
    private String email;
    private Gender gender;
}
