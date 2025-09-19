package com.priya.reviewservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when an operation is attempted on a user in an invalid state.
 * <p>
 * Example: Trying to activate an already active user or deactivating a user
 * that is already inactive. It ensures business rules regarding user state
 * transitions are enforced.
 * </p>
 */
@Slf4j
@Schema(description = "Exception thrown when a user operation violates current state constraints")
public class InvalidUserStateException extends RuntimeException {

    /**
     * Constructs a new InvalidUserStateException with the specified detail message.
     *
     * @param message the detail message providing context on the invalid user state
     */
    public InvalidUserStateException(String message) {
        super(message);

        // Log immediately for traceability
        log.error("Invalid user state encountered: {}", message);
    }

}