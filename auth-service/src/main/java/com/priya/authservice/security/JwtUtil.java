package com.priya.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

/**
 * Utility class for generating, parsing, and validating JSON Web Tokens (JWTs).
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Generate JWTs with claims (username, role, expiration).</li>
 *   <li>Extract claims such as username and role.</li>
 *   <li>Validate tokens (signature, expiration, format).</li>
 * </ul>
 * <p>
 * Used by the AuthService for authentication and authorization purposes.
 */

@Slf4j
@Component
@Hidden
public class JwtUtil {

    /**
     * Secret key for signing JWTs (should be externalized to config in production).
     */
    private static final String jwtSecret = "qR7L2MZ3UuHhKxk6eZ4Lq9pE1D5c0S7p3WlQ8hYjR5tT2aXyZsWmErBvCqDpGfJx";

    /**
     * Expiration time in milliseconds (1 hour).
     */
    private static final long jwtExpirationMs = 3600000;

    /**
     * Retrieves the signing key used for JWT signing and validation.
     *
     * @return SecretKey instance
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a signed JWT containing the username and role.
     *
     * @param username the username of the authenticated user
     * @param role     the role of the authenticated user
     * @return a signed JWT as a String
     * @throws IllegalArgumentException if username or role is null/empty
     */
    public String generateToken(String username, String role) {

        if (Objects.isNull(username) || username.isBlank()) {
            log.error("Failed to generate token: username is null or empty");
            throw new IllegalArgumentException("Username must not be null or empty");
        }

        if (Objects.isNull(role) || role.isBlank()) {
            log.error("Failed to generate token: role is null or empty");
            throw new IllegalArgumentException("Role must not be null or empty");
        }

        String token = Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();

        log.info("Generated JWT for user: {}, role: {}", username, role);

        return token;
    }

    /**
     * Extracts the username (subject) from a JWT.
     *
     * @param token the JWT
     * @return the username if present, otherwise null
     */
    public String extractUsername(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (JwtException e) {
            log.warn("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the role from a JWT.
     *
     * @param token the JWT
     * @return the role if present, otherwise null
     */
    public String extractRole(String token) {
        try {
            return parseClaims(token).get("role", String.class);
        } catch (JwtException e) {
            log.warn("Failed to extract role from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validates a JWT by checking its signature and expiration.
     *
     * @param token the JWT
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            if (claims.getExpiration().before(new Date())) {
                log.warn("JWT is expired for subject: {}", claims.getSubject());
                return false;
            }
            log.debug("JWT successfully validated for subject: {}", claims.getSubject());
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("Invalid JWT: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while validating JWT", e);
        }
        return false;
    }


    /**
     * Parses claims from the JWT.
     *
     * @param token the JWT
     * @return Claims object
     * @throws JwtException if parsing fails
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

