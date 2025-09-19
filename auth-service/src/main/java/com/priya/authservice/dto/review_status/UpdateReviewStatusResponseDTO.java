package com.priya.authservice.dto.review_status;

import com.priya.authservice.enums.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Response payload after updating review status.
 * Contains reviewId, updated status, and response message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO after updating review status")
public class UpdateReviewStatusResponseDTO {

    @Schema(description = "Unique identifier of the review", example = "R1001")
    private String reviewId;

    @Schema(description = "Updated status of the review", example = "APPROVED")
    private ReviewStatus status;

    @Schema(description = "Response message indicating success or reason for failure", example = "Status updated successfully")
    private String message;
}
