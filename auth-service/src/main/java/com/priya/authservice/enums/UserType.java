package com.priya.authservice.enums;


import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * Defines high-level categories of users in the system.
 *
 * <p>Used for classification, not for security (see {@link RoleName} for access control).</p>
 */
@Getter
@Slf4j
@Hidden
public enum UserType {

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
    NORMAL,

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
    HOTEL_MANAGER

}
