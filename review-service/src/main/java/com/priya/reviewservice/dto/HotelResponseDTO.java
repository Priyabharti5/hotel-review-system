package com.priya.reviewservice.dto;

import com.priya.reviewservice.enums.HotelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for mapping hotel data fetched from hotel-service via Feign client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hotel details fetched from Hotel Service")
public class HotelResponseDTO {

    @Schema(description = "Unique Hotel ID", example = "1234567890")
    private String hotelId;

    @Schema(description = "Average rating of the hotel", example = "4.5")
    private Double rating;

    @Schema(description = "Username of the hotel owner", example = "1234567890")
    private String ownerUsername;

    @Schema(description = "Current status of the hotel", example = "ACTIVE")
    private HotelStatus status;

}
