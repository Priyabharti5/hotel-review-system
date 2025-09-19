package com.priya.hotelservice.dto;

import com.priya.hotelservice.enums.HotelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for updating a hotel's status.
 * <p>
 * Returned after a status change operation (e.g., ACTIVE → INACTIVE).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload for hotel status update")
public class UpdateHotelStatusResponseDTO {

    @Schema(description = "Unique identifier of the hotel", example = "hotel123456")
    private String hotelId;

    @Schema(description = "Current status of the hotel", example = "ACTIVE")
    private HotelStatus status;

    @Schema(description = "Message describing the result of the operation", example = "Status updated successfully")
    private String message;

}
