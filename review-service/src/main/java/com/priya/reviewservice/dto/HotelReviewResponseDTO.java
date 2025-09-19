package com.priya.reviewservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a hotel review response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Review details for a Hotel")
public class HotelReviewResponseDTO {


    @Schema(description = "Unique Review ID", example = "9012345678")
    private String reviewId;

    @Schema(description = "Unique User ID who submitted the review", example = "9012345678")
    private String userId;

    @Schema(description = "Hotel ID associated with the review", example = "9012345678")
    private String hotelId;

    @Schema(description = "User's review comment", example = "Amazing stay, highly recommended!")
    private String comment;

    @Schema(description = "Rating given by the user", example = "4.8")
    private Double rating;

}
