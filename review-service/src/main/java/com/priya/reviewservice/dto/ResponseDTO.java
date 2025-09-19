package com.priya.reviewservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response wrapper used for simple success messages.
 * Can be reused across different API success responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic response for status messages")
public class ResponseDTO {

    /**
     * Custom status code (e.g., "200 OK", "201 CREATED").
     */
    @Schema(description = "HTTP status code", example = "200")
    private String statusCode;

    /**
     * Message describing the result of the operation.
     */
    @Schema(description = "Response status message", example = "Student deleted successfully")
    private String statusMessage;

}
