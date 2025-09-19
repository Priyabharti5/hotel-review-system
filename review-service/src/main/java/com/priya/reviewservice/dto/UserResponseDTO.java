package com.priya.reviewservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for mapping user data fetched from user-service via Feign client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User details fetched from User Service")
public class UserResponseDTO {

    @Schema(description = "Unique User ID", example = "1234567890")
    private String userId;

    @Schema(description = "User's full name", example = "Priya Bharti")
    private String name;

    @Schema(description = "User's email address", example = "priya@example.com")
    private String email;

    @Schema(description = "User's mobile number", example = "9876543210")
    private String mobile;

    @Schema(description = "About the user", example = "Travel blogger and food lover")
    private String about;

}
