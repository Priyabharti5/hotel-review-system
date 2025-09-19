package com.priya.reviewservice.utils;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * Utility class for generating 10-digit unique reviewId strings.
 * <p>
 * This class ensures that all generated IDs are strictly numeric
 * and exactly 10 digits long.
 * </p>
 * <p>
 * Usage: Call {@link #generateReviewId()} whenever a new unique review ID is required.
 * </p>
 */
@Hidden
@Slf4j
public class ReviewIdGeneratorUtil {

    private static final Random RANDOM = new Random();
    private static final long MIN = 1_000_000_000L; // Smallest 10-digit number
    private static final long MAX = 9_999_999_999L; // Largest 10-digit number

    // Private constructor to prevent instantiation
    private ReviewIdGeneratorUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Generates a random 10-digit review ID.
     * <p>
     * The ID will always be numeric and exactly 10 digits long.
     * </p>
     *
     * @return 10-digit numeric reviewId as a String
     * @throws IllegalStateException if generated ID is invalid (not 10 digits)
     */
    public static String generateReviewId() {
        long number = MIN + ((long) (RANDOM.nextDouble() * (MAX - MIN)));
        String reviewId = String.valueOf(number);

        // Validation to ensure safety
        if (reviewId.length() != 10) {
            log.error("Generated invalid reviewId [{}]. Expected 10 digits.", reviewId);
            throw new IllegalStateException("Generated reviewId is invalid: " + reviewId);
        }

        log.debug("Generated reviewId [{}]", reviewId);

        return reviewId;

    }
}
