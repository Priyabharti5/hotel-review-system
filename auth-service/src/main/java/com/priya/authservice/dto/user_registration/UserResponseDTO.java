package com.priya.authservice.dto.user_registration;

import com.priya.authservice.enums.UserStatus;
import com.priya.authservice.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for sending user details in API responses.
 * Includes audit fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload for user details")
public class UserResponseDTO {

    /**
     * Unique 10-digit user ID.
     */
    @Schema(description = "10-digit unique user identifier", example = "1234567890")
    private String userId;

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

    private UserStatus userStatus;

    private UserType userType;

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
