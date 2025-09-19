package com.priya.reviewservice.service;

import com.priya.reviewservice.dto.*;

import java.util.List;

/**
 * Service interface for managing {@code Review} entities.
 * <p>
 * Defines the business operations related to creating, retrieving,
 * updating, deleting, and analyzing hotel reviews.
 * <p>
 * <b>Note:</b> Methods suffixed with {@code _internal} are intended for
 * inter-service communication via Feign clients and are not exposed
 * directly to external API consumers.
 */
public interface ReviewService {

    /**
     * Creates a new review.
     *
     * @param requestDTO input review data
     * @return created review response
     */
    ReviewResponseDTO createReview(ReviewRequestDTO requestDTO);

    /**
     * Retrieves a review by its reviewId.
     *
     * @param reviewId unique review identifier
     * @return review response
     */
    ReviewResponseDTO getReviewById(String reviewId);

    /**
     * Retrieves all reviews.
     *
     * @return list of all reviews
     */
    List<ReviewResponseDTO> getAllReviews();

    /**
     * Retrieves all reviews for a given hotel.
     *
     * @param hotelId unique hotel identifier
     * @return list of reviews for the hotel
     */
    List<ReviewResponseDTO> getReviewsByHotelId(String hotelId);

    /**
     * Retrieves all reviews by a given user.
     *
     * @param userId unique user identifier
     * @return list of reviews by the user
     */
    List<ReviewResponseDTO> getReviewsByUserId(String userId);

    /**
     * Updates a review by its reviewId.
     *
     * @param reviewId   review to update
     * @param requestDTO new data
     * @return updated review response
     */
    ReviewResponseDTO updateReviewComment(String reviewId, UpdateReviewCommentDTO requestDTO);

    /**
     * Deletes a review by its unique identifier.
     *
     * @param reviewId the unique review identifier
     * @return a response indicating success/failure of deletion
     */
    ResponseDTO deleteReview(String reviewId);

    /**
     * Calculates the average rating for a given hotel.
     *
     * @param hotelId the unique hotel identifier
     * @return the average rating, or {@code null} if no reviews exist
     */
    Double getAverageRatingByHotelId(String hotelId);

    /**
     * Retrieves the userId associated with a given review.
     * <p><b>Internal use only</b> - used for service-to-service communication.</p>
     *
     * @param reviewId the unique review identifier
     * @return userId response DTO
     */
    UserIdResponseDTO getUserIdByReviewId_internal(String reviewId);

    /**
     * Retrieves all hotel reviews created by a given user.
     * <p><b>Internal use only</b> - used for service-to-service communication.</p>
     *
     * @param userId the unique user identifier
     * @return list of hotel reviews submitted by the user
     */
    List<HotelReviewResponseDTO> getReviewsByUserId_internal(String userId);

    /**
     * Retrieves the hotelId associated with a given review.
     * <p><b>Internal use only</b> - used for service-to-service communication.</p>
     *
     * @param reviewId the unique review identifier
     * @return the associated hotelId
     */
    String getHotelIdByReviewId_internal(String reviewId);

    /**
     * Updates the status of a review (e.g., APPROVED, REJECTED).
     * <p><b>Internal use only</b> - used for service-to-service communication.</p>
     *
     * @param requestDTO the update request containing reviewId and new status
     * @return response DTO with updated status
     */
    UpdateReviewStatusResponseDTO updateReviewStatus_internal(UpdateReviewStatusRequestDTO requestDTO);

}
