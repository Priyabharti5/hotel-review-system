package com.priya.hotelservice.dto;

import com.priya.hotelservice.enums.UserStatus;
import com.priya.hotelservice.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for validating user access or role within the hotel service.
 * <p>
 * Indicates whether a user is a valid hotel owner and their account status.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response payload for user validation in hotel service")
public class UserValidationResponseDTO {

    @Schema(description = "Username of the user", example = "1234567890")
    private String username;

    @Schema(description = "Type of the user", example = "HOTEL_OWNER")
    private UserType userType;

    @Schema(description = "Status of the user", example = "ACTIVE")
    private UserStatus userStatus;

    @Schema(description = "Validation result message", example = "User is valid and active")
    private String message;

    @Schema(description = "Flag indicating if the user is a valid hotel owner", example = "true")
    private Boolean validHotelOwner;

}
