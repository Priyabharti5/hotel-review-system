package com.priya.authservice.security;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * In-memory JWT token store for managing active tokens.
 * <p>
 * This component is responsible for tracking issued JWT tokens
 * (e.g., on login) and invalidating them (e.g., on logout).
 * Used primarily for blacklist/whitelist checks in stateless JWT authentication.
 * <p>
 * Note: This implementation is in-memory and thread-safe.
 * It should be replaced with a distributed store (e.g., Redis)
 * for production in a multi-instance deployment.
 */

@Slf4j
@Component
@Hidden
public class JwtTokenStore {

    /**
     * Thread-safe set holding currently active tokens.
     */
    private final Set<String> activeTokens = Collections.synchronizedSet(new HashSet<>());

    /**
     * Adds a JWT token to the active store.
     *
     * @param token JWT access token string (must not be null or blank)
     */
    public void addToken(String token) {
        if (!StringUtils.hasText(token)) {
            log.warn("Attempted to add a null or blank token to store");
            return;
        }
        activeTokens.add(token);
        log.debug("Token added to active store. Current active token count: {}", activeTokens.size());

    }

    /**
     * Removes a JWT token from the active store.
     * Typically called during logout or token invalidation.
     *
     * @param token JWT access token string (must not be null or blank)
     */
    public void removeToken(String token) {

        if (!StringUtils.hasText(token)) {
            log.warn("Attempted to remove a null or blank token from store");
            return;
        }
        boolean removed = activeTokens.remove(token);
        if (removed) {
            log.debug("Token removed from active store. Current active token count: {}", activeTokens.size());
        } else {
            log.debug("Attempted to remove non-existent token from active store");
        }

    }

    /**
     * Checks if a JWT token is currently active.
     *
     * @param token JWT access token string (must not be null or blank)
     * @return true if token is in the active store, false otherwise
     */
    public boolean isTokenActive(String token) {

        if (!StringUtils.hasText(token)) {
            log.warn("Attempted to check activity of null or blank token");
            return false;
        }
        boolean active = activeTokens.contains(token);
        log.trace("Token [{}] active status: {}", token, active);
        return active;

    }

}
