package com.priya.authservice.dto.admin_registration;

import com.priya.authservice.enums.UserStatus;
import com.priya.authservice.enums.RoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload after successful Admin User Creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload after successful Admin User Creation.")
public class AdminRegisterResponseDTO {

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

    /**
     * Role assigned to the user.
     */
    @Schema(description = "Role of the authenticated user", example = "ROLE_USER")
    private RoleName roleName;

}