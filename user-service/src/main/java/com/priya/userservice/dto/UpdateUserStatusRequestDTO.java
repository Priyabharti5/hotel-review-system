package com.priya.userservice.dto;

import com.priya.userservice.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO to update the status of a user.
 * Ensures that both username and new status are provided.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to update the status of a user")
public class UpdateUserStatusRequestDTO {

    /**
     * New status to assign to the user.
     * Must be one of ACTIVE, SUSPENDED, DELETED, or EXPIRED.
     */
    @Schema(description = "Status to update the user to", example = "ACTIVE")
    @NotNull(message = "Status Must NOT be Null!")
    private UserStatus status;

    /**
     * Username of the user whose status is being updated.
     */
    @Schema(description = "Unique username of the user", example = "1234567890")
    @NotBlank(message = "userName must not be null")
    private String userName;

}