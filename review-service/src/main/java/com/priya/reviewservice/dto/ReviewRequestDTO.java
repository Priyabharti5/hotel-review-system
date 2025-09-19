package com.priya.reviewservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for accepting review creation or update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating or updating a review")
public class ReviewRequestDTO {

    /**
     * User ID who is giving the review.
     */
    @Schema(description = "10-digit unique user identifier", example = "1234567890")
    @NotBlank(message = "User ID must not be blank")
    @Size(min = 10, max = 10, message = "User ID must be exactly 10 characters")
    private String userId;

    /**
     * Hotel ID being reviewed.
     */
    @Schema(description = "10-digit unique hotel identifier", example = "9876543210")
    @NotBlank(message = "Hotel ID must not be blank")
    @Size(min = 10, max = 10, message = "Hotel ID must be exactly 10 characters")
    private String hotelId;

    /**
     * Rating given by the user (1.0 to 5.0 scale).
     */
    @Schema(description = "Rating given to the hotel (1.0 to 5.0)", example = "4.5")
    @NotNull(message = "Rating must not be null")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
    private Double rating;

    /**
     * Optional comment about the hotel.
     */
    @Schema(description = "User's comment about the hotel", example = "Great service and clean rooms.")
    @Size(max = 1000, message = "Comment can have at most 1000 characters")
    private String comment;

}
