package com.toyota.authenticationauthorizationservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user management response.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserManagementResponse {
    private String username;
    private String email;
}
