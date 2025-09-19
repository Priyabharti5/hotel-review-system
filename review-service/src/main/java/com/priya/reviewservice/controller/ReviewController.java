package com.priya.reviewservice.controller;

import com.priya.reviewservice.dto.*;
import com.priya.reviewservice.service.ReviewService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing review-related operations.
 * <p>
 * Supported operations:
 * <ul>
 *   <li>Create a new review</li>
 *   <li>Retrieve review(s) by ID, userId, or hotelId</li>
 *   <li>Fetch all reviews</li>
 *   <li>Update or delete reviews</li>
 *   <li>Compute average hotel rating</li>
 * </ul>
 * <p>
 * Internal endpoints are provided for inter-service communication and should be secured in production.
 * <p>
 * Base path: <b>/api/reviews</b>
 *
 * @author Priya
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Review APIs", description = "Operations related to Reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Create a new review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO requestDTO) {
        log.info("API Call: Create review request={}", requestDTO);
        ReviewResponseDTO createdReview = reviewService.createReview(requestDTO);
        log.info("API Success: Created reviewId={} for userId={} hotelId={}", createdReview.getReviewId(), createdReview.getUserId(), createdReview.getHotelId());
        return ResponseEntity.status(201).body(createdReview);
    }

    @Operation(summary = "Get review by Review ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review found",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable("reviewId") String reviewId) {
        log.info("API Call: Get review by reviewId={}", reviewId);
        ReviewResponseDTO response = reviewService.getReviewById(reviewId);
        log.info("API Success: Fetched reviewId={} for userId={}", response.getReviewId(), response.getUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of reviews fetched successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponseDTO.class))))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN','HOTEL_MANAGER')")    // restrict to admin
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        log.info("API Call: Fetch all reviews");
        List<ReviewResponseDTO> reviews = reviewService.getAllReviews();
        log.info("API Success: Fetched {} reviews", reviews.size());
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get all reviews by a User")
    @GetMapping("/user/{userId}")
    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')") // only owner or admin can access
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUserId(@PathVariable("userId") String userId) {
        log.info("API Call: Get reviews for userId={}", userId);
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByUserId(userId);
        log.info("API Success: Found {} reviews for userId={}", reviews.size(), userId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get all reviews for a Hotel")
    @GetMapping("/hotel/{hotelId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','HOTEL_MANAGER')") // anyone authenticated can view reviews
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByHotelId(@PathVariable("hotelId") String hotelId) {
        log.info("API Call: Get reviews for hotelId={}", hotelId);
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByHotelId(hotelId);
        log.info("API Success: Found {} reviews for hotelId={}", reviews.size(), hotelId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Update an existing review comment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated successfully",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{reviewId}")
    @PreAuthorize("@reviewSecurity.isReviewOwner(#reviewId, authentication.name)")
    public ResponseEntity<ReviewResponseDTO> updateReviewComment(
            @PathVariable("reviewId") String reviewId,
            @Valid @RequestBody UpdateReviewCommentDTO requestDTO) {
        log.info("API Call: Update reviewId={}, request={}", reviewId, requestDTO);
        ReviewResponseDTO updatedReview = reviewService.updateReviewComment(reviewId, requestDTO);
        log.info("API Success: Updated reviewId={} comment", updatedReview.getReviewId());
        return ResponseEntity.ok(updatedReview);
    }

    @Operation(summary = "Delete a review by Review ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteReview(@PathVariable("reviewId") String reviewId) {
        log.info("API Call: Delete reviewId={}", reviewId);
        ResponseDTO responseDTO = reviewService.deleteReview(reviewId);
        log.info("API Success: Deleted reviewId={}", reviewId);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Get average rating for a hotel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Average rating calculated successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/hotel/{hotelId}/avg-rating")
    @PreAuthorize("hasAnyRole('USER','ADMIN','HOTEL_MANAGER')")
    public ResponseEntity<Map<String, Double>> getAverageRatingByHotelId(@PathVariable("hotelId") String hotelId) {
        log.info("API Call: Get average rating for hotelId={}", hotelId);
        double avgRating = reviewService.getAverageRatingByHotelId(hotelId);
        log.info("API Success: Avg rating for hotelId={} = {}", hotelId, avgRating);
        return ResponseEntity.ok(Map.of("rating", avgRating));
    }

    /**
     * Internal: Fetch userId for a given reviewId.
     * Should be secured in production.
     */
    @Hidden
    @GetMapping("/_internal/{reviewId}")
    public ResponseEntity<UserIdResponseDTO> getUserIdByReviewId_internal(@PathVariable("reviewId") String reviewId) {
        log.info("[Internal]: API Call: Fetching userId for given reviewId: [{}]", reviewId);
        UserIdResponseDTO responseDTO = reviewService.getUserIdByReviewId_internal(reviewId);
        log.info("[Internal]: API Success: Fetched userId: [{}] for given reviewId: [{}]", responseDTO.getUserId(), reviewId);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Internal endpoint used by other services (feign) to fetch reviews by userId.
     * Note: keep internal endpoints protected in production (mutual TLS, OAuth2 client credentials, etc.)
     */
    @Hidden
    @GetMapping("/_internal/user/{userId}")
    public ResponseEntity<List<HotelReviewResponseDTO>> getReviewsByUserId_internal(@PathVariable("userId") String userId) {
        log.info("[Internal]: API Call: Fetch reviews for userId={}", userId);
        List<HotelReviewResponseDTO> reviews = reviewService.getReviewsByUserId_internal(userId);
        log.info("[Internal]: API Success: Found {} reviews for userId={}", reviews.size(), userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Internal: Fetch hotelId for a given reviewId (used by other services).
     */
    @Hidden
    @GetMapping("/_internal/review/{reviewId}")
    public ResponseEntity<String> getHotelIdByReviewId_internal(@PathVariable("reviewId") String reviewId) {
        log.info("[Internal]: API Call: Fetch hotelId for reviewId={}", reviewId);
        String hotelId = reviewService.getHotelIdByReviewId_internal(reviewId);
        log.info("[Internal]: API Success: hotelId={} for reviewId={}", hotelId, reviewId);
        return ResponseEntity.ok(hotelId);
    }

    /**
     * Internal: Update review status (used by other services).
     */
    @Hidden
    @PutMapping("/_internal/review/status")
    public ResponseEntity<UpdateReviewStatusResponseDTO> updateReviewStatus_internal(
            @Valid @RequestBody UpdateReviewStatusRequestDTO requestDTO) {

        log.info("[Internal]: API Call: Update review status request={}", requestDTO);
        UpdateReviewStatusResponseDTO responseDTO = reviewService.updateReviewStatus_internal(requestDTO);
        log.info("[Internal]: API Success: Updated status for reviewId={} to {}", responseDTO.getReviewId(), responseDTO.getStatus());
        return ResponseEntity.ok(responseDTO);
    }

}
