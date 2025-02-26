package com.toyota.authenticationauthorizationservice.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for token response.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private String token;
}
