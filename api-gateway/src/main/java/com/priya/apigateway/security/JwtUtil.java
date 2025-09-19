package com.priya.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    private static final String jwtSecret = "qR7L2MZ3UuHhKxk6eZ4Lq9pE1D5c0S7p3WlQ8hYjR5tT2aXyZsWmErBvCqDpGfJx";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Extract username (subject)
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract role claim
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Validate token (signature + expiry)
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Parse claims helper
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token.replace("Bearer ", ""))
                .getPayload();
    }
}
