package com.toyota.authenticationauthorizationservice.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO which stores old and new password for updating.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordRequest {
    @NotNull(message = "Old password must not be null")
    @NotBlank(message = "Old password must not be blank")
    String oldPassword;
    @Size(min = 8, message = "New password must be at least 8 characters")
    @NotNull(message = "New password must not be null")
    @NotBlank(message = "New password must not be blank")
    String newPassword;
}
