package com.priya.hotelservice.client;

import com.priya.hotelservice.config.FeignConfig;
import com.priya.hotelservice.dto.HotelReviewResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client for communication with the Review Service.
 * Provides access to review-related internal endpoints
 * for retrieving reviews and resolving hotel associations.
 */
@FeignClient(
        name = "review-service",
        configuration = FeignConfig.class,
        path = "/api/reviews"
)
@Hidden
public interface ReviewServiceClient {

    /**
     * Retrieve all reviews submitted by a specific user.
     *
     * @param userId the ID of the user
     * @return list of {@link HotelReviewResponseDTO}
     */
    @GetMapping("_internal/user/{userId}")
    List<HotelReviewResponseDTO> getReviewsByUserId(@PathVariable("userId") String userId);

    /**
     * Retrieve the hotel ID associated with a specific review.
     *
     * @param reviewId the review ID
     * @return hotel ID as String
     */
    @GetMapping("/_internal/review/{reviewId}")
    String getHotelIdByReviewId(@PathVariable("reviewId") String reviewId);

}
