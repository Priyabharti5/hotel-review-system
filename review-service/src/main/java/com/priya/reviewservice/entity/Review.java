package com.priya.reviewservice.entity;

import com.priya.reviewservice.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

/**
 * Represents a Review entity in the Hotel Review System.
 * Stores user feedback and rating for a specific hotel.
 */
@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = "reviewId"))
//@SQLRestriction("status = 'ACTIVE'")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Review extends BaseEntity {

    /**
     * Primary key of the review (internal use).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Unique business review ID.
     */
    @Column(name = "review_id", nullable = false, unique = true, length = 10, updatable = false)
    private String reviewId;

    /**
     * ID of the user who gave the review (references user-service).
     */
    @Column(name = "user_id", nullable = false, length = 10)
    private String userId;

    /**
     * ID of the hotel being reviewed (references hotel-service).
     */
    @Column(name = "hotel_id", nullable = false, length = 10)
    private String hotelId;

    /**
     * Rating given by the user (1.0 to 5.0 scale).
     * this value should be updated to the respective hotel rating after success
     */
    @Column(name = "rating", nullable = false, updatable = false)
    private Double rating;  // immutable after create

    /**
     * Optional comment for the review.
     */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    /**
     * Lifecycle status of the review (ACTIVE, HIDDEN, DELETED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReviewStatus status;

}