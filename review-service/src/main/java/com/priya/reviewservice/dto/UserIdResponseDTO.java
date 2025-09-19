package com.priya.reviewservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing a User ID and additional message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing User ID details")
public class UserIdResponseDTO {

    @Schema(description = "Unique User ID", example = "USR12345")
    private String userId;

    @Schema(description = "Additional message", example = "User found successfully")
    private String message;
}
