package com.priya.authservice.dto.user_status;

import com.priya.authservice.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request payload to update the status of a user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to update the status of a user")
public class UpdateUserStatusRequestDTO {

    /**
     * New status to assign to the user. Must be one of ACTIVE, SUSPENDED, DELETED, or EXPIRED.
     */
    @Schema(description = "Status to update the user to", example = "ACTIVE")
    @NotNull(message = "Status Must NOT be Null!")
    private UserStatus status;

    @NotBlank(message = "Username must not be blank!")
    @Size(min = 3, max = 10, message = "UserName Must be 3-10 Chars!")
    private String userName;
}