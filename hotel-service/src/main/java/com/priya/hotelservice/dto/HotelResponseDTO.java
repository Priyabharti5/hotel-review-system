package com.priya.hotelservice.dto;

import com.priya.hotelservice.enums.HotelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for sending hotel details in API responses.
 * Hides internal database ID and focuses on business-level fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for returning hotel details")
public class HotelResponseDTO {

    /**
     * Unique business hotel ID.
     */
    @Schema(description = "Unique 10-digit hotel ID", example = "1234567890")
    private String hotelId;

    /**
     * Name of the hotel.
     */
    @Schema(description = "Name of the hotel", example = "The Grand Palace")
    private String name;

    /**
     * Location or address of the hotel.
     */
    @Schema(description = "Location of the hotel", example = "Mumbai, India")
    private String location;

    /**
     * Description about the hotel.
     */
    @Schema(description = "Description of the hotel", example = "Luxury hotel with sea view")
    private String about;

    /**
     * Average rating of the hotel.
     */
    @Schema(description = "Average rating of the hotel", example = "4.5")
    private Double rating;

    /**
     * Creation timestamp.
     */
    @Schema(description = "Timestamp when the hotel was created", example = "2025-08-01T14:30:00")
    private LocalDateTime createdAt;

    /**
     * Last updated timestamp.
     */
    @Schema(description = "Timestamp when the hotel was last updated", example = "2025-08-05T10:15:00")
    private LocalDateTime updatedAt;

    @Schema(description = "User ID of the hotel owner", example = "1234567890")
    private String ownerUsername;

    @Schema(description = "Hotel Status of the hotel", example = "ACTIVE")
    private HotelStatus status;
}
