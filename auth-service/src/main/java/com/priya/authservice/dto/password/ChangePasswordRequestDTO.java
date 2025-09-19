package com.priya.authservice.dto.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request payload for changing a user's password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for changing user password")
public class ChangePasswordRequestDTO {

    @Schema(description = "Unique username of the user", example = "1234567890")
    @NotBlank(message = "Username must not be blank!")
    @Size(min = 3, max = 10, message = "UserName Must be 3-10 Chars!")
    private String userName;

    @Schema(description = "Current password", example = "Old@123")
    @NotBlank(message = "Old password must not be blank")
    @Size(min = 5, max = 64, message = "Old Password Must be 5-64 Chars!")
    private String oldPassword;

    @Schema(description = "New password", example = "Secure@123")
    @NotBlank(message = "New password must not be blank")
    @Size(min = 5, max = 64, message = "New Password Must be 5-64 Chars!")
    private String newPassword;
}
