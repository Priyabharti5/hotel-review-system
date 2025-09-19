package com.priya.reviewservice.mapper;

import com.priya.reviewservice.dto.ReviewRequestDTO;
import com.priya.reviewservice.dto.ReviewResponseDTO;
import com.priya.reviewservice.entity.Review;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Mapper utility for converting between {@link Review} entities and DTOs.
 * <p>
 * Ensures a clean separation between persistence models and API contracts.
 * Designed as a utility class with static methods only.
 * </p>
 */
@Slf4j
@Hidden
public class ReviewMapper {

    // Private constructor to prevent instantiation
    private ReviewMapper() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Converts a {@link Review} entity to a {@link ReviewResponseDTO}.
     *
     * @param review the review entity (must not be {@code null})
     * @return the mapped {@link ReviewResponseDTO}
     * @throws IllegalArgumentException if {@code review} is null
     */
    public static ReviewResponseDTO toDTO(Review review) {
        Objects.requireNonNull(review, "Review entity must not be null");

        log.debug("Mapping Review entity to ReviewResponseDTO [reviewId={}]", review.getReviewId());

        return ReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .hotelId(review.getHotelId())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    /**
     * Converts a {@link ReviewRequestDTO} to a {@link Review} entity.
     * <p>
     * Performs basic validation on required fields before mapping.
     * </p>
     *
     * @param dto the request DTO (must not be {@code null})
     * @return the mapped {@link Review} entity
     * @throws IllegalArgumentException if {@code dto} is null or mandatory fields are missing
     */
    public static Review toEntity(ReviewRequestDTO dto) {
        Objects.requireNonNull(dto, "ReviewRequestDTO must not be null");

        log.debug("Mapping ReviewRequestDTO to Review entity [userId={}, hotelId={}]", dto.getUserId(), dto.getHotelId());

        return Review.builder()
                .userId(dto.getUserId())
                .hotelId(dto.getHotelId())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();
    }
}
