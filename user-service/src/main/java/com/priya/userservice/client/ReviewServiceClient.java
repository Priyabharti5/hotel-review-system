package com.priya.userservice.client;

import com.priya.userservice.config.FeignConfig;
import com.priya.userservice.dto.UserIdResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for retrieving review-related user information from the Review Service.
 * <p>
 * This client is hidden from API documentation since it is used for internal service-to-service
 * communication only (via Feign + Eureka).
 * </p>
 */
@FeignClient(
        name = "review-service",
        configuration = FeignConfig.class,
        path = "/api/reviews"
)
@Validated
@Hidden
public interface ReviewServiceClient {

    /**
     * Fetches the userId associated with a given reviewId.
     *
     * @param reviewId the review identifier (must not be blank)
     * @return a DTO containing the userId of the review author
     */
    @GetMapping("/_internal/{reviewId}")
    UserIdResponseDTO getUserIdByReviewId(@PathVariable("reviewId") String reviewId);

}
