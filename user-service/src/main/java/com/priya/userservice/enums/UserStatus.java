package com.priya.userservice.enums;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enumeration representing the lifecycle status of an authenticated user.
 * <p>
 * This status determines whether a user is eligible to authenticate
 * and perform actions within the system. It is primarily controlled by
 * administrators or system events (e.g., account expiry).
 *
 * <h3>Status Transition Rules</h3>
 * <ul>
 *   <li>{@link #ACTIVE} → {@link #SUSPENDED}: by ADMIN</li>
 *   <li>{@link #SUSPENDED} → {@link #ACTIVE}: by ADMIN</li>
 *   <li>{@link #ACTIVE} → {@link #DELETED}: by ADMIN or optionally self-delete</li>
 *   <li>{@link #ACTIVE} → {@link #EXPIRED}: by SYSTEM (account expiry)</li>
 *   <li>{@link #EXPIRED} → {@link #ACTIVE}: by ADMIN (override expiry)</li>
 *   <li>{@link #SUSPENDED} → {@link #DELETED}: by ADMIN</li>
 * </ul>
 *
 * <h3>Logging Recommendations</h3>
 * Always log the transition with context:
 * <pre>
 * log.info("User [{}] status changed from [{}] to [{}] by [{}]",
 *          username, oldStatus, newStatus, actor);
 * </pre>
 *
 * <h3>Validation Rules</h3>
 * <ul>
 *   <li>DTOs should mark this field with {@code @NotNull}.</li>
 *   <li>Illegal transitions (e.g., {@link #DELETED} → {@link #ACTIVE})
 *       must be validated at the service layer.</li>
 * </ul>
 */
@Hidden
@Getter
@Slf4j
public enum UserStatus {

    /**
     * User is active and allowed to log in.
     */
    ACTIVE("Active and allowed to login"),

    /**
     * User is temporarily suspended by admin.
     * Cannot log in until reactivated.
     */
    SUSPENDED("Temporarily suspended by admin"),

    /**
     * User is soft-deleted. Login is permanently blocked
     * unless admin restores the account.
     */
    DELETED("Soft deleted, not allowed to login"),

    /**
     * User account expired due to system rules (e.g. subscription end).
     * Requires admin override to reactivate.
     */
    EXPIRED("Account expired, not allowed to login");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }
}
