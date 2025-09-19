package com.priya.hotelservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO representing a hotel review.
 * <p>
 * Used for returning review details to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload for hotel reviews")
public class HotelReviewResponseDTO {

    @Schema(description = "Unique identifier for the review", example = "1234567890")
    private String reviewId;

    @Schema(description = "Unique ID of the user who submitted the review", example = "1234567890")
    private String userId;

    @Schema(description = "Unique ID of the hotel being reviewed", example = "1234567890")
    private String hotelId;

    @Schema(description = "Comment provided by the user", example = "Excellent stay with great service")
    private String comment;

    @Schema(description = "Rating given by the user (1-5)", example = "5")
    private double rating;

}
