package com.priya.authservice.dto.hotel_status;

import com.priya.authservice.enums.HotelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for updating the status of a hotel.
 * Must include hotelId and the desired new status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to update the status of a hotel")
public class UpdateHotelStatusRequestDTO {

    /**
     * New status to assign to the hotel. Must be one of ACTIVE, INACTIVE, DELETED, or BLOCKED.
     */
    @Schema(description = "Status to update the hotel to", example = "ACTIVE")
    @NotNull(message = "Status Must NOT be Null!")
    private HotelStatus status;

    @Schema(description = "New status to assign to the hotel", example = "ACTIVE")
    @NotNull(message = "hotelId must not be null")
    private String hotelId;

}