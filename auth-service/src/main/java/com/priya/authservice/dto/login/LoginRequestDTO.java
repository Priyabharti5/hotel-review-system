package com.priya.authservice.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for user login.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for Auth-User Login")
public class LoginRequestDTO {

    /**
     * UserName of Auth-User
     */
    @Schema(description = "Unique UserName of Auth-User", example = "User456780")
    @NotBlank(message = "UserName of Auth-User Must NOT be Blank!")
    @Size(min = 3, max = 10, message = "UserName Must be 3-10 Chars!")
    private String userName;

    /**
     * Password of Auth-User for Authentication
     */
    @Schema(description = "Password of Auth-User", example = "GJS@783")
    @NotBlank(message = "Password Must Not be Blank")
    @Size(min = 5, max = 64, message = "Password Must be 5-64 Chars!")
    private String password;
}