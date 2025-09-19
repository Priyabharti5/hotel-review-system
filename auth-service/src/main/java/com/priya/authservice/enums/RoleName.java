package com.priya.authservice.enums;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enumeration representing the different roles available in the authentication and authorization system.
 *
 * <p>These roles determine user access permissions across the application.</p>
 *
 * <ul>
 *     <li>{@link #ROLE_USER} – Standard end user with basic privileges.</li>
 *     <li>{@link #ROLE_HOTEL_MANAGER} – Business user who manages hotel data.</li>
 *     <li>{@link #ROLE_ADMIN} – System administrator with full access.</li>
 * </ul>
 *
 * <p><b>Usage:</b> Always use this enum for type safety instead of hardcoding role strings.</p>
 */
@Getter
@Slf4j
@Hidden
public enum RoleName {

    /**
     * Standard application user.
     * <p>
     * Typical privileges:
     * <ul>
     *     <li>Can register, login, logout.</li>
     *     <li>Can update own profile.</li>
     *     <li>Can write reviews and view hotels.</li>
     * </ul>
     */
    ROLE_USER,

    /**
     * Hotel manager role.
     * <p>
     * Typical privileges:
     * <ul>
     *     <li>Can manage hotel information.</li>
     *     <li>Can respond to reviews.</li>
     *     <li>Restricted to hotels they own/manage.</li>
     * </ul>
     */
    ROLE_HOTEL_MANAGER,

    /**
     * System administrator role.
     * <p>
     * Typical privileges:
     * <ul>
     *     <li>Full access to all APIs.</li>
     *     <li>Can suspend, delete, or reactivate users.</li>
     *     <li>Can manage system-wide configurations.</li>
     * </ul>
     */
    ROLE_ADMIN;

    /**
     * Logs the usage of the role. Useful for debugging or auditing role assignments.
     *
     * @return the name of the role
     */
    public String logUsage() {
        String roleName = this.name();
        log.debug("Role [{}] is being referenced", roleName);
        return roleName;
    }

}

