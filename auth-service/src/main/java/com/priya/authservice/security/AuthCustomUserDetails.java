package com.priya.authservice.security;

import com.priya.authservice.entity.Role;
import com.priya.authservice.entity.AuthUser;
import com.priya.authservice.enums.UserStatus;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of Spring Security's {@link UserDetails}.
 * <p>
 * Wraps an {@link AuthUser} entity to integrate with Spring Security authentication
 * and authorization mechanisms. Provides user authorities, password, username,
 * and account state validations (expired, locked, suspended, deleted).
 * </p>
 *
 * <h2>Production Considerations:</h2>
 * <ul>
 *   <li>Integrates with {@code UserDetailsService} for authentication.</li>
 *   <li>Checks user status ({@link UserStatus}) to determine login eligibility.</li>
 *   <li>Provides role-based authorities for authorization.</li>
 *   <li>Logs important lifecycle decisions for debugging/auditing.</li>
 * </ul>
 */

@AllArgsConstructor
@Getter
@Slf4j
@Hidden
public class AuthCustomUserDetails implements UserDetails {

    /**
     * Wrapped AuthUser entity that contains credentials, role, and status.
     */
    private final AuthUser user;

    /**
     * Returns granted authorities based on the user's role.
     *
     * @return a singleton collection containing the user's role
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = user.getRole();
        if (role == null) {
            log.warn("User [{}] has no role assigned", user.getUsername());
            return Collections.emptyList();
        }
        log.trace("Assigning role [{}] to user [{}]", role.getName().name(), user.getUsername());
        return Collections.singleton(new SimpleGrantedAuthority(role.getName().name()));
    }

    /**
     * Returns the user's hashed password.
     *
     * @return password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username (unique identifier for login).
     *
     * @return username
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the account has expired.
     *
     * @return {@code true} if account is not expired, otherwise {@code false}
     */
    @Override
    public boolean isAccountNonExpired() {
        boolean result = !UserStatus.EXPIRED.equals(user.getStatus());
        if (!result) {
            log.warn("User [{}] account expired", user.getUsername());
        }
        return result;
    }


    /**
     * Indicates whether the account is not locked (suspended).
     *
     * @return {@code true} if not suspended, otherwise {@code false}
     */
    @Override
    public boolean isAccountNonLocked() {
        boolean result = !UserStatus.SUSPENDED.equals(user.getStatus());
        if (!result) {
            log.warn("User [{}] account is suspended", user.getUsername());
        }
        return result;
    }

    /**
     * Indicates whether the credentials are valid (not expired).
     *
     * @return {@code true} always, since credentials expiration is not tracked separately
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the account is enabled (not deleted).
     *
     * @return {@code true} if active, otherwise {@code false}
     */
    @Override
    public boolean isEnabled() {
        boolean result = UserStatus.ACTIVE.equals(user.getStatus());
        if (!result) {
            log.warn("User [{}] not enabled due to status [{}]", user.getUsername(), user.getStatus());
        }
        return result;
    }


    /**
     * Returns the status of the current user.
     *
     * @return status of the {@link AuthUser}
     */
    public UserStatus getStatus() {
        return user.getStatus();
    }

}
