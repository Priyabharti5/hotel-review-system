package com.priya.authservice.enums;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * Enum representing the lifecycle status of a Review entity.
 * <p>
 * Provides better clarity and extensibility compared to boolean flags.
 * </p>
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

}
