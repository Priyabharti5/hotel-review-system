package com.priya.authservice.exception;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when user authentication fails due to invalid credentials.
 * <p>
 * This is typically thrown when username/password do not match.
 * It is handled at the global exception handler level and translated into
 * a structured API error response.
 * </p>
 */
@Schema(description = "Exception thrown when authentication fails due to invalid credentials")
@Slf4j
public class InvalidCredentialsException extends RuntimeException {


    /**
     * Constructs a new InvalidCredentialsException with the specified detail message.
     *
     * @param message the detail message providing context on the invalid credentials error
     */
    public InvalidCredentialsException(String message) {
        super(message);

        // Log immediately for traceability
        log.warn("Invalid credentials attempt detected: {}", message);
    }

}