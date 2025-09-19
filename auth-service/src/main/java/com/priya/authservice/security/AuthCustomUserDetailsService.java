package com.priya.authservice.security;

import com.priya.authservice.entity.AuthUser;
import com.priya.authservice.repository.AuthUserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Custom implementation of {@link UserDetailsService} for authentication.
 * <p>
 * This service is used internally by Spring Security to load user-specific data
 * during authentication. It fetches the {@link AuthUser} entity from the database
 * and maps it to a {@link AuthCustomUserDetails} object.
 * </p>
 *
 * <p><b>Note:</b> This class is a Spring Security component and is not exposed as a REST endpoint.</p>
 */
@RequiredArgsConstructor
@Service
@Slf4j
@Hidden
public class AuthCustomUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    /**
     * Loads the user details by username for authentication.
     *
     * @param username the username (unique 10-digit user identifier)
     * @return {@link UserDetails} representing the authenticated user
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Objects.requireNonNull(username, "Username must not be null");

        log.debug("AUTH SERVICE: Attempting to load user by username [{}]", username);


        AuthUser user = authUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("AUTH SERVICE: User not found with username [{}]", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        log.info("AUTH SERVICE: Successfully loaded user [{}] with role [{}] and status [{}]",
                user.getUsername(), user.getRole().getName(), user.getStatus());

        return new AuthCustomUserDetails(user);
    }
}
