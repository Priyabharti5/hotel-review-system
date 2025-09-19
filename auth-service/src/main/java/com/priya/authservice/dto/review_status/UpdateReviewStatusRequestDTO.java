package com.priya.authservice.dto.review_status;

import com.priya.authservice.enums.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request payload to update a review's status.
 * Includes reviewId and the new status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for updating review status")
public class UpdateReviewStatusRequestDTO {

    @Schema(description = "Unique identifier of the review", example = "R1001")
    @NotBlank(message = "ReviewId is mandatory")
    private String reviewId;

    @Schema(description = "New status to assign to the review", example = "APPROVED")
    @NotNull(message = "Status must not be null")
    private ReviewStatus status;

}
