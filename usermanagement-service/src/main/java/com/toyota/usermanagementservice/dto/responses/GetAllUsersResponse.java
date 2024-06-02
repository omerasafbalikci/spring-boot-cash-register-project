package com.toyota.usermanagementservice.dto.responses;

import com.toyota.usermanagementservice.domain.Gender;
import com.toyota.usermanagementservice.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for user used as response.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUsersResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private boolean deleted;
    private Set<Role> roles;
    private Gender gender;
}
