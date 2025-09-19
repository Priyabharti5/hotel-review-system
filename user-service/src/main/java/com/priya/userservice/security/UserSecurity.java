package com.priya.userservice.security;

import com.priya.userservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Security helper class for User entity-specific authorization checks.
 * <p>
 * Provides methods to verify if the current authenticated user
 * is allowed to perform operations on a user resource (e.g., self-ownership).
 */
@Component("userSecurity")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class UserSecurity {

    private final UserRepository userRepository;

    /**
     * Checks if the current user is the same as the requested userId.
     * Typically used to ensure normal users can only access their own data.
     *
     * @param userId        Business User ID of the resource to validate.
     * @param currentUserId Username (or userId) of the currently authenticated user.
     * @return true if current user is the same as the target user, false otherwise.
     */
    public boolean isSelfUser(String userId, String currentUserId) {
        if (userId == null || currentUserId == null) {
            log.warn("Either target userId or currentUserId is null!");
            return false;
        }

        return userRepository.findByUserId(userId)
                .map(user -> user.getUserId().equals(currentUserId))
                .orElse(false);
    }

    /**
     * Check if the logged-in user is the same as the one queried by email.
     */
    public boolean isSelfUserByEmail(String email, String loggedInUserId) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    boolean isSelf = user.getUserId().equals(loggedInUserId);
                    if (!isSelf) {
                        log.warn("Access denied: loggedInUserId={} tried to access another user by email={}",
                                loggedInUserId, email);
                    }
                    return isSelf;
                })
                .orElse(false);
    }

    /**
     * Check if the logged-in user is the same as the one queried by mobile.
     */
    public boolean isSelfUserByMobile(String mobile, String loggedInUserId) {
        return userRepository.findByMobile(mobile)
                .map(user -> {
                    boolean isSelf = user.getUserId().equals(loggedInUserId);
                    if (!isSelf) {
                        log.warn("Access denied: loggedInUserId={} tried to access another user by mobile={}",
                                loggedInUserId, mobile);
                    }
                    return isSelf;
                })
                .orElse(false);
    }

    /**
     * Utility to check if current user has ROLE_ADMIN.
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }

    /**
     * Checks if a user exists for the given userId.
     * Useful for validation before performing actions.
     *
     * @param userId Business User ID.
     * @return true if user exists, false otherwise.
     */
    public boolean doesUserExist(String userId) {
        return userRepository.existsByUserId(userId);
    }
}
