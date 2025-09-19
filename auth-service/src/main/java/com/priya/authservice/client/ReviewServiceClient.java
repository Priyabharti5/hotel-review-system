package com.priya.authservice.client;

import com.priya.authservice.config.FeignConfig;
import com.priya.authservice.dto.review_status.UpdateReviewStatusRequestDTO;
import com.priya.authservice.dto.review_status.UpdateReviewStatusResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with the {@code review-service}.
 * <p>
 * This client is used internally to update review status during user/hotel lifecycle changes.
 */
@FeignClient(
        name = "review-service",
        configuration = FeignConfig.class,
        path = "/api/reviews")
@Hidden
public interface ReviewServiceClient {

    /**
     * Calls Review Service to update review status.
     *
     * @param requestDTO request containing reviewId and new status
     * @return response DTO confirming updated review status
     */
    @PutMapping("/_internal/review/status")
    UpdateReviewStatusResponseDTO updateReviewStatus_internal(@RequestBody UpdateReviewStatusRequestDTO requestDTO);
}
