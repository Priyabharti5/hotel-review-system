package com.priya.reviewservice.dto;

import com.priya.reviewservice.enums.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for response after updating a review status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response after review status update")
public class UpdateReviewStatusResponseDTO {

    @Schema(description = "Unique Review ID", example = "REV987654")
    private String reviewId;

    @Schema(description = "Updated status of the review", example = "APPROVED")
    private ReviewStatus status;

    @Schema(description = "Additional message about the update", example = "Review status updated successfully")
    private String message;

}
