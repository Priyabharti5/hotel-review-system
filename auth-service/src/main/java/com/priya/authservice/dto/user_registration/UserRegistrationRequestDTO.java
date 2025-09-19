package com.priya.authservice.dto.user_registration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request DTO payload for Registering new User
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for registering a user")
public class UserRegistrationRequestDTO {

    @Schema(description = "Full name of the user", example = "Priya Bharti")
    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 30, message = "Name must be 2–30 characters")
    private String name;

    @Schema(description = "Mobile number of the user", example = "9876543210")
    @NotBlank(message = "Mobile number must not be blank")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be 10 digits starting with 6-9")
    private String mobile;

    @Schema(description = "Email address of the user", example = "priya@example.com")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Short bio or description about the user", example = "Travel enthusiast and reviewer.")
    @Size(max = 255, message = "About section can have at most 255 characters")
    private String about;

    @Schema(description = "Password for account login", example = "Secure@123")
    @NotBlank(message = "Password must not be blank")
    @Size(min = 5, max = 64, message = "Password Must be 5-64 Chars!")
    private String password;

}
