package com.priya.userservice.dto;

import com.priya.userservice.enums.UserStatus;
import com.priya.userservice.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for returning user details.
 * Includes audit fields (createdAt, updatedAt).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload for user details with audit fields")
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

    @Schema(description = "Status of the user", example = "ACTIVE")
    private UserStatus userStatus;

    @Schema(description = "Type of the user", example = "CUSTOMER")
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
