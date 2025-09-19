package com.priya.userservice.utils;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * Utility class for generating 10-digit unique userId strings.
 */
@Hidden
@Slf4j
public class UserIdGeneratorUtil {

    private static final Random RANDOM = new Random();
    private static final long MIN = 1_000_000_000L; // Smallest 10-digit number
    private static final long MAX = 9_999_999_999L; // Largest 10-digit number

    // Private constructor to prevent instantiation
    private UserIdGeneratorUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Generates a random 10-digit user ID.
     * <p>
     * The ID will always be numeric and exactly 10 digits long.
     * </p>
     *
     * @return 10-digit numeric userId as a String
     * @throws IllegalStateException if generated ID is invalid (not 10 digits)
     */
    public static String generateUserId() {
        long number = MIN + ((long) (RANDOM.nextDouble() * (MAX - MIN)));
        String userId = String.valueOf(number);

        // Validation to ensure safety
        if (userId.length() != 10) {
            log.error("Generated invalid reviewId [{}]. Expected 10 digits.", userId);
            throw new IllegalStateException("Generated reviewId is invalid: " + userId);
        }

        log.debug("Generated reviewId [{}]", userId);
        return userId;
    }
}
