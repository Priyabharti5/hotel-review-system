package com.priya.reviewservice.repository;

import com.priya.reviewservice.entity.Review;
import com.priya.reviewservice.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Review} entities.
 * <p>
 * Provides standard CRUD operations along with custom finder methods
 * for queries involving review identifiers, hotel associations,
 * user associations, and review status filters.
 * </p>
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Find a review by its unique business identifier.
     *
     * @param reviewId the unique review identifier
     * @return optional containing the review if found, otherwise empty
     */
    Optional<Review> findByReviewId(String reviewId);

    /**
     * Find a review by its unique identifier and status.
     *
     * @param reviewId the unique review identifier
     * @param status   the review status (e.g., ACTIVE, INACTIVE)
     * @return optional containing the review if matching record is found
     */
    Optional<Review> findByReviewIdAndStatus(String reviewId, ReviewStatus status);

    /**
     * Find all reviews associated with a specific hotel.
     *
     * @param hotelId the unique hotel identifier
     * @return list of reviews, may be empty
     */
    List<Review> findByHotelId(String hotelId);

    /**
     * Find all reviews for a specific hotel filtered by status.
     *
     * @param hotelId the unique hotel identifier
     * @param status  the review status
     * @return list of reviews, may be empty
     */
    List<Review> findByHotelIdAndStatus(String hotelId, ReviewStatus status);

    /**
     * Find all reviews written by a specific user.
     *
     * @param userId the unique user identifier
     * @return list of reviews, may be empty
     */
    List<Review> findByUserId(String userId);

    /**
     * Delete a review by its unique business identifier.
     *
     * @param reviewId the unique review identifier
     */
    void deleteByReviewId(String reviewId);

    /**
     * Check if a review already exists for a given user and hotel.
     *
     * @param userId  the user identifier
     * @param hotelId the hotel identifier
     * @return true if review exists, false otherwise
     */
    boolean existsByUserIdAndHotelId(String userId, String hotelId);

    /**
     * Calculate the average rating of active reviews for a given hotel.
     *
     * @param hotelId the hotel identifier
     * @return the average rating, or null if no active reviews exist
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hotelId = :hotelId AND r.status = 'ACTIVE'")
    Double getAverageRatingByHotelId(@Param("hotelId") String hotelId);

    /**
     * Find all reviews for multiple hotels.
     *
     * @param hotelIds list of hotel identifiers
     * @return list of reviews, may be empty
     */
    List<Review> findByHotelIdIn(List<String> hotelIds);

    /**
     * Find all reviews filtered by status.
     *
     * @param status the review status
     * @return list of reviews, may be empty
     */
    List<Review> findByStatus(ReviewStatus status);

    /**
     * Find all reviews written by a user filtered by status.
     *
     * @param userId the user identifier
     * @param status the review status
     * @return list of reviews, may be empty
     */
    List<Review> findByUserIdAndStatus(String userId, ReviewStatus status);

}
