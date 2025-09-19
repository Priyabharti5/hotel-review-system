package com.priya.userservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when a requested resource (e.g., User, Hotel, Review)
 * cannot be found in the system.
 *
 * <p>This exception should be caught by a global exception handler
 * and mapped to an appropriate HTTP response, typically {@code 404 Not Found}.
 *
 * <p>Example scenarios:
 * <ul>
 *     <li>Fetching a user by ID that does not exist</li>
 *     <li>Requesting a review that has been deleted</li>
 * </ul>
 */
@Slf4j
@Schema(description = "Exception thrown when a requested resource is not found")
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceNotFoundException} with the specified detail message.
     *
     * @param message detail message describing which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
        log.warn("ResourceNotFoundException: {}", message);
    }

    /**
     * Constructs a new {@code ResourceNotFoundException} with the specified detail message and cause.
     *
     * @param message detail message describing which resource was not found
     * @param cause   the underlying cause of this exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        log.warn("ResourceNotFoundException: {}, cause: {}", message, cause.getMessage(), cause);
    }
}