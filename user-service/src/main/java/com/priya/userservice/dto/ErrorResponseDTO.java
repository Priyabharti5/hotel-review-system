package com.priya.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard format for sending error responses to the client.
 * Useful for global exception handling.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for representing API error response")
public class ErrorResponseDTO {

    /**
     * Timestamp when the error occurred.
     */
    @Schema(description = "Time when the error occurred", example = "2025-08-02T12:45:00")
    private LocalDateTime timeStamp;

    /**
     * HTTP status code (e.g., 404, 500).
     */
    @Schema(description = "HTTP status code", example = "404")
    private Integer status;

    /**
     * Error type (e.g., "Not Found", "Internal Server Error").
     */
    @Schema(description = "HTTP error message", example = "NOT_FOUND")
    private String error;

    /**
     * Developer-friendly error message.
     */
    @Schema(description = "Detailed error message", example = "Student not found with ID: 5")
    private String message;

    /**
     * Request path where the error occurred.
     */
    @Schema(description = "Request path where the error occurred", example = "/api/students/5")
    private String path;

}
