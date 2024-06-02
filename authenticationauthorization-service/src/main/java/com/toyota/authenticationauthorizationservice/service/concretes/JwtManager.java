package com.toyota.authenticationauthorizationservice.service.concretes;

import com.toyota.authenticationauthorizationservice.service.abstracts.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Service implementation for managing jwt.
 */

@Service
@AllArgsConstructor
public class JwtManager implements JwtService {
    @Value("${application.security.jwt.secret-key}")
    private final String SECRET_KEY;
    @Value("${application.security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * Generates token
     * @param userDetails User details
     * @return Token
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = Map.of("jti", UUID.randomUUID().toString());
        return generateToken(claims, userDetails);
    }

    /**
     * Generates jwt token
     * @param extraClaims Extra Claims
     * @param userDetails User information
     * @return Token
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + this.jwtExpiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts username
     * @param jwt Token
     * @return Username
     */
    @Override
    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    /**
     * Extracts token ID
     * @param jwt Token
     * @return Token ID
     */
    @Override
    public String extractTokenId(String jwt) {
        Claims claims = extractAllClaims(jwt);
        return claims.get("jti").toString();
    }

    /**
     * Extracts a specific claim from the provided JWT token using the given claims resolver function.
     *
     * @param <T> the type of the claim to be extracted
     * @param token the JWT token from which to extract the claim
     * @param claimsResolver a function that defines how to extract the desired claim from the token's claims
     * @return the extracted claim of type T
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims
     * @param token Token
     * @return Claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * @return Signing key
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(this.SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Checks if token is valid
     * @param jwt Token
     * @param userDetails User information
     * @return boolean: if token is valid or not.
     */
    @Override
    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);
        return (username.equals(userDetails.getUsername())) && (!isTokenExpired(jwt));
    }

    /**
     * Checks if token is expired
     * @param token Token
     * @return  boolean
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts expiration date
     * @param token token
     * @return  Date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
