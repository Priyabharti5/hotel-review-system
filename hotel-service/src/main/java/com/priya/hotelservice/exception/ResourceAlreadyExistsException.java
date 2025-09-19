package com.priya.hotelservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when attempting to create or update a resource
 * that violates a uniqueness constraint (e.g., duplicate email, mobile, username).
 *
 * <p>This exception should be caught by a global exception handler
 * and mapped to an appropriate HTTP response, typically {@code 409 Conflict}.
 *
 * <p>Example scenarios:
 * <ul>
 *     <li>Registering a user with an already registered email or mobile number</li>
 *     <li>Creating a hotel with a duplicate hotel code</li>
 * </ul>
 */

@Slf4j
@Schema(description = "Exception thrown when attempting to create or update a resource with duplicate values")
public class ResourceAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceAlreadyExistsException} with the specified detail message.
     *
     * @param message detail message describing the duplicate violation
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
        log.error("ResourceAlreadyExistsException: {}", message);
    }

    /**
     * Constructs a new {@code ResourceAlreadyExistsException} with the specified detail message and cause.
     *
     * @param message detail message describing the duplicate violation
     * @param cause   the underlying cause of this exception
     */
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
        log.error("ResourceAlreadyExistsException: {}, cause: {}", message, cause.getMessage(), cause);
    }
}