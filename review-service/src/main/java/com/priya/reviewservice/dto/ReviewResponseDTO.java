package com.priya.reviewservice.dto;

import com.priya.reviewservice.enums.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for sending review details in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload for review details")
public class ReviewResponseDTO {

    /**
     * Unique 10-digit review ID.
     */
    @Schema(description = "10-digit unique review identifier", example = "1122334455")
    private String reviewId;

    /**
     * User ID who gave the review.
     */
    @Schema(description = "10-digit unique user identifier", example = "1234567890")
    private String userId;

    /**
     * Hotel ID being reviewed.
     */
    @Schema(description = "10-digit unique hotel identifier", example = "9876543210")
    private String hotelId;

    /**
     * Rating given by the user.
     */
    @Schema(description = "Rating given to the hotel (1.0 to 5.0)", example = "4.5")
    private Double rating;

    /**
     * Comment about the hotel.
     */
    @Schema(description = "User's comment about the hotel", example = "Great service and clean rooms.")
    private String comment;

    private ReviewStatus status;

    /**
     * Timestamp when the review record was created (audit).
     */
    @Schema(description = "Timestamp when review was created in system", example = "2025-08-09T10:30:00")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the review record was last updated (audit).
     */
    @Schema(description = "Timestamp when review was last updated", example = "2025-08-09T14:45:00")
    private LocalDateTime updatedAt;

}
