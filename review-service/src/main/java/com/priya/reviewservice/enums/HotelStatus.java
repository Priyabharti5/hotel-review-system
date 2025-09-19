package com.priya.reviewservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.priya.reviewservice.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Enum representing the lifecycle/status of a Hotel entity.
 *
 * <p>Use this enum instead of a boolean flag for soft deletes and
 * lifecycle management. This ensures future extensibility and clear semantics.</p>
 *
 * <ul>
 *     <li>{@link #ACTIVE}   → Hotel is listed and bookable.</li>
 *     <li>{@link #INACTIVE} → Hotel exists but not available for booking.</li>
 *     <li>{@link #DELETED}  → Hotel is soft-deleted and hidden from queries.</li>
 *     <li>{@link #BLOCKED}  → Hotel is blacklisted due to violations.</li>
 * </ul>
 */

@Getter
@Slf4j
@Hidden
public enum HotelStatus {

    ACTIVE("ACTIVE", "Hotel is active and available for booking"),

    INACTIVE("INACTIVE", "Hotel exists but not available for booking"),

    DELETED("DELETED", "Hotel is soft-deleted and excluded from listings"),

    BLOCKED("BLOCKED", "Hotel is blocked due to policy violations");

    private final String code;
    private final String description;

    HotelStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Deserialize from string safely (case-insensitive).
     *
     * @param code status code as String
     * @return matching HotelStatus
     * @throws ResourceNotFoundException if no match found
     */
    @JsonCreator
    public static HotelStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(status -> status.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Invalid HotelStatus code: " + code));
    }

    /**
     * Serialize to string for APIs/DB.
     *
     * @return status code
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}

