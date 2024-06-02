package com.toyota.authenticationauthorizationservice.service.abstracts;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface for jwt service class.
 */

public interface JwtService {
    /**
     * Generates a JWT token based on the provided user details.
     *
     * @param userDetails the user details to include in the JWT token
     * @return the generated JWT token
     */
    String generateToken(UserDetails userDetails);

    /**
     * Extracts the username from the provided JWT token.
     *
     * @param jwt the JWT token from which to extract the username
     * @return the username extracted from the JWT token
     */
    String extractUsername(String jwt);

    /**
     * Extracts the token ID from the provided JWT token.
     *
     * @param jwt the JWT token from which to extract the token ID
     * @return the token ID extracted from the JWT token
     */
    String extractTokenId(String jwt);

    /**
     * Validates the provided JWT token against the given user details.
     *
     * @param jwt the JWT token to validate
     * @param userDetails the user details to validate against
     * @return true if the JWT token is valid, false otherwise
     */
    boolean isTokenValid(String jwt, UserDetails userDetails);
}
