package com.priya.reviewservice.enums;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum representing the lifecycle status of a Review.
 *
 * <p>Used instead of a boolean flag for better clarity and extensibility.</p>
 * <ul>
 *   <li>{@link #ACTIVE} - Review is visible and considered active.</li>
 *   <li>{@link #HIDDEN} - Review is temporarily hidden (e.g., under moderation).</li>
 *   <li>{@link #DELETED} - Review has been soft-deleted (not shown to users).</li>
 * </ul>
 */
@Getter
@Slf4j
@Hidden
public enum ReviewStatus {

    /**
     * Review is visible and considered active.
     */
    ACTIVE("Active"),

    /**
     * Review is temporarily hidden (e.g., under moderation).
     */
    HIDDEN("Hidden"),

    /**
     * Review has been soft-deleted (not shown to users).
     */
    DELETED("Deleted");

    private final String displayName;

    ReviewStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Case-insensitive parser to map input string to {@link ReviewStatus}.
     *
     * @param value String value to parse
     * @return matching {@link ReviewStatus}
     * @throws IllegalArgumentException if the value does not match any status
     */
    public static ReviewStatus fromString(String value) {
        for (ReviewStatus status : values()) {
            if (status.name().equalsIgnoreCase(value) ||
                    status.displayName.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ReviewStatus value: " + value);
    }

}
