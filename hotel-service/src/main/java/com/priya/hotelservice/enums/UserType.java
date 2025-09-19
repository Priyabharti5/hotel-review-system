package com.priya.hotelservice.enums;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enumeration of user types in the system.
 * <p>
 * This classification helps in assigning roles and access
 * levels to different categories of users.
 *
 * <h3>Usage</h3>
 * <ul>
 *   <li>{@link #NORMAL} → End users such as hotel guests and reviewers.</li>
 *   <li>{@link #HOTEL_MANAGER} → Business users who manage hotels.</li>
 * </ul>
 *
 * <h3>Logging & Validation</h3>
 * <ul>
 *   <li>Always log role/type assignment during user creation or update.</li>
 *   <li>DTOs referencing this enum should be validated with {@code @NotNull}.</li>
 * </ul>
 */
@Getter
@Hidden
@Slf4j
public enum UserType {
    /**
     * Represents a general user of the system, e.g.,
     * hotel guest or reviewer who books hotels and submits reviews.
     */
    NORMAL,

    /**
     * Represents a hotel manager user who manages hotel properties
     * and related operations.
     */
    HOTEL_MANAGER

}
