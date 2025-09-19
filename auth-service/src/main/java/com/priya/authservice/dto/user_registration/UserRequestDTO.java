package com.priya.authservice.dto.user_registration;

import com.priya.authservice.enums.UserStatus;
import com.priya.authservice.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for accepting user creation or update requests.
 * Contains basic personal details without audit info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating or updating a user")
public class UserRequestDTO {

    /**
     * Full name of the user.
     */
    @Schema(description = "Full name of the user", example = "Priya Bharti")
    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 30, message = "Name must be 2–30 characters")
    private String name;

    /**
     * Mobile number (must be 10 digits starting with 6-9).
     */
    @Schema(description = "Mobile number of the user", example = "9876543210")
    @NotBlank(message = "Mobile number must not be blank")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be 10 digits starting with 6-9")
    private String mobile;

    /**
     * Email address.
     */
    @Schema(description = "Email address of the user", example = "priya@example.com")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Optional short bio or about section.
     */
    @Schema(description = "Short bio or description about the user", example = "Travel enthusiast and reviewer.")
    @Size(max = 255, message = "About section can have at most 255 characters")
    private String about;

    @Schema(description = "Account status of the user", example = "ACTIVE")
    private UserStatus userStatus;

    @Schema(description = "Type of the user (e.g., HOTEL_MANAGER, USER)", example = "USER")
    private UserType userType;

}
