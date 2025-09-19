package com.priya.hotelservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.priya.hotelservice.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum representing comparison operators for hotel ratings.
 * <p>
 * Used to filter hotels based on their rating with operators like greater than, less than, or equal.
 * Supports JSON serialization/deserialization via {@link JsonValue} and {@link JsonCreator}.
 */
@Getter
@AllArgsConstructor
@Slf4j
@Hidden
public enum RatingOperator {

    /**
     * Represents "greater than" comparison.
     */
    GREATER_THAN("gt", "Greater than"),

    /**
     * Represents "less than" comparison.
     */
    LESS_THAN("lt", "Less than"),

    /**
     * Represents "equal to" comparison.
     */
    EQUAL("eq", "Equal to");


    /**
     * Operator code used in API requests (e.g., "gt", "lt", "eq").
     */
    private final String code;

    /**
     * Human-readable description of the operator.
     */
    private final String description;

    /**
     * Returns the operator code used for JSON serialization.
     *
     * @return the operator code as String
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * Case-insensitive factory method to create {@link RatingOperator} from a code string.
     *
     * @param code operator code from API request
     * @return corresponding RatingOperator
     * @throws ResourceNotFoundException if code is invalid
     */
    @JsonCreator
    public static RatingOperator fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Rating operator code cannot be null or blank");
        }

        for (RatingOperator op : values()) {
            if (op.code.equalsIgnoreCase(code)) {
                return op;
            }
        }
        throw new ResourceNotFoundException("Invalid rating operator: " + code + ". Supported values: gt, lt, eq");
    }
}
