package com.priya.authservice.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload after successful login.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload after successful login")
public class LoginResponseDTO {

    /**
     * JWT token issued after authentication.
     */
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String token;

    /**
     * Token type, typically "Bearer".
     */
    @Schema(description = "Type of the token", example = "Bearer")
    private String type;

    /**
     * Unique username of the authenticated user.
     */
    @Schema(description = "Unique username of the user", example = "1234567890")
    private String userName;

}