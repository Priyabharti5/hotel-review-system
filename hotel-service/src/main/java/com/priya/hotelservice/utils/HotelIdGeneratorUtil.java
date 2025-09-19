package com.priya.hotelservice.utils;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * Utility class for generating unique 10-digit hotel IDs.
 * <p>
 * Provides methods to generate hotel IDs for business use. The generated IDs are numeric
 * and 10 digits long. This class is stateless and thread-safe for concurrent calls.
 * <p>
 * <strong>Note:</strong> Collision handling should be implemented at persistence level if required.
 */
@Hidden
@Slf4j
public class HotelIdGeneratorUtil {

    private static final Random random = new Random();
    private static final long MIN = 1_000_000_000L;  // Smallest 10-digit number
    private static final long MAX = 9_999_999_999L;  // Largest 10-digit number

    // Private constructor to prevent instantiation
    private HotelIdGeneratorUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Generates a random 10-digit numeric hotel ID.
     *
     * @return a 10-digit numeric hotelId as a String
     * @throws IllegalStateException if the generated ID is out of range (should not occur)
     */
    public static String generateHotelId() {
        long number = MIN + ((long) (random.nextDouble() * (MAX - MIN)));

        if (number < MIN || number > MAX) {
            log.error("Generated hotelId [{}] is out of valid range ({}-{})", number, MIN, MAX);
            throw new IllegalStateException("Generated hotelId is out of valid 10-digit range");
        }

        String hotelId = String.valueOf(number);
        log.debug("Generated hotelId: {}", hotelId);

        return hotelId;
    }
}
