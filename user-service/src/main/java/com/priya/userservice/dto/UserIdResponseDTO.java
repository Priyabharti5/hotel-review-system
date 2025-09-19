package com.priya.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned when fetching user ID from Review-Service
 * or validating user existence.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload containing userId and operation message")
public class UserIdResponseDTO {

    @Schema(description = "Unique identifier of the user", example = "1234567890")
    private String userId;

    @Schema(description = "Operation result message", example = "User found successfully")
    private String message;

}
