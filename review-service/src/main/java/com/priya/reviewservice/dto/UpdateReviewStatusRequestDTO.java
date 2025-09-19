package com.priya.reviewservice.dto;

import com.priya.reviewservice.enums.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for updating the status of a review.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for updating review status")
public class UpdateReviewStatusRequestDTO {

    @NotBlank(message = "ReviewId is mandatory")
    @Schema(description = "Unique Review ID", example = "1234567890")
    private String reviewId;

    @NotNull(message = "Status must not be null")
    @Schema(description = "New status for the review", example = "ACTIVE")
    private ReviewStatus status;

}
