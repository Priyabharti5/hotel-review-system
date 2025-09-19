package com.priya.authservice.dto.hotel_status;

import com.priya.authservice.enums.HotelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Response DTO returned after updating a hotel's status.
 * Contains the hotel identifier, updated status, and a message for client feedback.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO for updating Hotel Status")
public class UpdateHotelStatusResponseDTO {

    @Schema(description = "Unique identifier of the hotel", example = "H1001")
    private String hotelId;

    @Schema(description = "Updated status of the hotel", example = "ACTIVE")
    private HotelStatus status;

    @Schema(description = "Response message indicating success or reason for failure", example = "Status updated successfully")
    private String message;

}
