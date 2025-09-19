package com.priya.authservice.dto.user_status;

import com.priya.authservice.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload returned after updating an Auth-User's status.
 * Contains the username, updated account status, and response message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload after successful Auth-User status update")
public class UpdateUserStatusResponseDTO {

    /**
     * Unique username of the authenticated user.
     */
    @Schema(description = "Unique userName of the user", example = "1234567890")
    private String userName;

    /**
     * Current account status.
     */
    @Schema(description = "Account status of the user", example = "ACTIVE")
    private UserStatus status;

    @Schema(description = "Response message indicating success or reason for failure", example = "Status updated successfully")
    private String message;

}