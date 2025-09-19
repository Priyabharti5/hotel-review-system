package com.priya.authservice.dto.user_registration;

import com.priya.authservice.enums.RoleName;
import com.priya.authservice.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO after successfully User registration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload after successful user registration")
public class UserRegistrationResponseDTO {

    /**
     * Unique 10-digit user ID (business identifier, used as username for login).
     */
    @Schema(description = "10-digit unique user identifier", example = "1234567890")
    private String userName;

    /**
     * Full name of the user.
     */
    @Schema(description = "Full name of the user", example = "Priya Bharti")
    private String name;

    /**
     * Mobile number.
     */
    @Schema(description = "User's mobile number", example = "9876543210")
    private String mobile;

    /**
     * Email address.
     */
    @Schema(description = "User's email address", example = "priya@example.com")
    private String email;

    /**
     * Short bio or about section.
     */
    @Schema(description = "User's short bio or about section", example = "Travel enthusiast and reviewer.")
    private String about;

    /**
     * Account enabled status (true = active, false = disabled).
     */
    @Schema(description = "Account status of the user", example = "ACTIVE")
    private UserStatus status;

    /**
     * Role assigned to the user.
     */
    @Schema(description = "Role assigned to the user", example = "ROLE_USER")
    private RoleName role;

    /**
     * Timestamp when the user was created.
     */
    @Schema(description = "Date when user was created", example = "2025-08-01T10:30:00")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user was last updated.
     */
    @Schema(description = "Date when user was last updated", example = "2025-08-02T14:45:00")
    private LocalDateTime updatedAt;
}
