package com.toyota.usermanagementservice.dto.responses;

import com.toyota.usermanagementservice.domain.Gender;
import com.toyota.usermanagementservice.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUsersResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Set<Role> roles;
    private Gender gender;
}
