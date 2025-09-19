package com.priya.userservice.dto;

import com.priya.userservice.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Response DTO after updating the status of a user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload after updating user status")
public class UpdateUserStatusResponseDTO {

    @Schema(description = "Username of the user", example = "priya123")
    private String userName;

    @Schema(description = "Updated status of the user", example = "SUSPENDED")
    private UserStatus status;

    @Schema(description = "Operation result message", example = "User status updated successfully")
    private String message;

}
