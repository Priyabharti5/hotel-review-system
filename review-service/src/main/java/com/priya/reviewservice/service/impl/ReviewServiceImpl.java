package com.priya.reviewservice.service.impl;

import com.priya.reviewservice.dto.*;
import com.priya.reviewservice.entity.Review;
import com.priya.reviewservice.enums.ReviewStatus;
import com.priya.reviewservice.exception.ResourceAlreadyExistsException;
import com.priya.reviewservice.exception.ResourceNotFoundException;
import com.priya.reviewservice.client.HotelServiceClient;
import com.priya.reviewservice.client.UserServiceClient;
import com.priya.reviewservice.mapper.ReviewMapper;
import com.priya.reviewservice.repository.ReviewRepository;
import com.priya.reviewservice.security.ReviewSecurity;
import com.priya.reviewservice.service.ReviewService;
import com.priya.reviewservice.utils.ReviewIdGeneratorUtil;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ReviewService}.
 * <p>
 * Handles business logic for managing reviews including:
 * <ul>
 *   <li>Creating reviews with validation against User and Hotel services.</li>
 *   <li>Fetching reviews with role-based access control (ADMIN, USER, HOTEL_MANAGER).</li>
 *   <li>Updating comments and statuses of reviews.</li>
 *   <li>Calculating average ratings for hotels.</li>
 *   <li>Internal methods for inter-service communication (via Feign clients).</li>
 * </ul>
 * <p>
 * All methods include logging, validation, and access control checks to ensure
 * production-grade reliability and auditability.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserServiceClient userServiceClient;
    private final HotelServiceClient hotelServiceClient;
    private final ReviewSecurity reviewSecurity;

    @Override
    public ReviewResponseDTO createReview(ReviewRequestDTO requestDTO) {
        log.info("Validating Review Request for User ID: [{}] and Hotel ID: [{}]", requestDTO.getUserId(), requestDTO.getHotelId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUser = reviewSecurity.getUserId(authentication);

        if (!loggedInUser.equals(requestDTO.getUserId())) {
            throw new AccessDeniedException("User can only create review for themselves");
        }
        if (reviewRepository.existsByUserIdAndHotelId(requestDTO.getUserId(), requestDTO.getHotelId())) {
            throw new ResourceAlreadyExistsException("User has already reviewed this hotel");
        }

        String userId;
        try {
            UserResponseDTO user = userServiceClient.getUserById(requestDTO.getUserId());
            userId = user.getUserId();
        } catch (FeignException e) {
            log.error("User service call failed for userId={}", requestDTO.getUserId(), e);
            throw new ResourceNotFoundException("User not found with id=" + requestDTO.getUserId());
        }

        // Validate Hotel -  using Feign Client
        String hotelId;
        try {
            HotelResponseDTO hotelResponseDTO = hotelServiceClient.getHotelByHotelId(requestDTO.getHotelId());
            hotelId = hotelResponseDTO.getHotelId();
        } catch (FeignException e) {
            log.error("Hotel service call failed for hotelId={}", requestDTO.getHotelId(), e);
            throw new ResourceNotFoundException("Hotel not found with id=" + requestDTO.getHotelId());
        }

        Review review = ReviewMapper.toEntity(requestDTO);
        review.setUserId(userId);
        review.setHotelId(hotelId);
        review.setReviewId(ReviewIdGeneratorUtil.generateReviewId());
        review.setStatus(ReviewStatus.ACTIVE);

        Review savedReview = reviewRepository.save(review);

        log.info("Review created successfully with ID: {}", savedReview.getId());

        // Recalculate & update hotel’s average rating
        Double avgRating = reviewRepository.getAverageRatingByHotelId(hotelId);
        if (avgRating == null) {
            avgRating = 0.0;
        }

        // updating final avg rating of the hotel after successful review via feign
        log.info("Updating Hotel: [{}] with new average rating: {}", hotelId, avgRating);
        try {
            hotelServiceClient.updateHotelRating(hotelId, avgRating);
        } catch (FeignException e) {
            log.error("Hotel service unavailable or hotel not found for id={}", hotelId, e);
            throw new ResourceNotFoundException("Hotel not found with id=" + hotelId);
        }

        return ReviewMapper.toDTO(savedReview);
    }

    @Override
    public ReviewResponseDTO getReviewById(String reviewId) {
        log.info("Fetching review by reviewId = {}", reviewId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = reviewSecurity.getUserId(authentication);
        String role = reviewSecurity.getUserRole(authentication);

        Review review;

        if (reviewSecurity.isAdmin(authentication)) {
            review = reviewRepository.findByReviewId(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Review not found with reviewId: " + reviewId));
        } else if (role.equals("ROLE_USER")) {

            review = reviewRepository.findByReviewIdAndStatus(reviewId, ReviewStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("Review not found with reviewId: " + reviewId));
            if (!review.getUserId().equals(userName)) {
                throw new AccessDeniedException("Access denied: not review owner");
            }
        } else {
            review = reviewRepository.findByReviewId(reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Review not found with reviewId: " + reviewId));
            HotelResponseDTO hotelResponseDTO = null;
            try {
                hotelResponseDTO = hotelServiceClient.getHotelByHotelId(review.getHotelId());
                if (userName.equals(hotelResponseDTO.getOwnerUsername())) {
                    return ReviewMapper.toDTO(review);
                } else {
                    throw new AccessDeniedException("Not access to view other hotel review");
                }
            } catch (FeignException e) {
                log.error("Hotel service call failed for hotelId={}", review.getHotelId(), e);
                throw new ResourceNotFoundException("Hotel not found with id=" + review.getHotelId());
            }
        }

        return ReviewMapper.toDTO(review);
    }

    @Override
    public List<ReviewResponseDTO> getAllReviews() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Fetching all reviews");
        String ownerId = reviewSecurity.getUserId(authentication);
        List<Review> reviews;

        if (reviewSecurity.isAdmin(authentication)) {
            reviews = reviewRepository.findAll();
        } else if (reviewSecurity.getUserRole(authentication).equals("ROLE_USER")) {
            reviews = reviewRepository.findByUserIdAndStatus(ownerId, ReviewStatus.ACTIVE);
        } else {
            try {
                List<String> ownedHotelIds = hotelServiceClient.getHotelIdsByOwnerUserName(ownerId);
                reviews = reviewRepository.findByHotelIdIn(ownedHotelIds);
            } catch (FeignException e) {
                log.error("Hotel service call failed for ownerUserName={}", ownerId, e);
                throw new ResourceNotFoundException("Hotels not found for ownerUserName=" + ownerId);
            }
        }

        if (reviews.isEmpty()) {
            log.warn("No Reviews found");
            throw new ResourceNotFoundException("No Reviews Found");
        }

        return reviews.stream()
                .map(ReviewMapper::toDTO)
                .toList();
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByHotelId(String hotelId) {
        log.info("Fetching reviews for hotelId: {}", hotelId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = reviewSecurity.getUserId(authentication);
        String role = reviewSecurity.getUserRole(authentication);

        // Validate hotelId -  using Feign Client
        HotelResponseDTO hotelResponseDTO = null;
        try {
            hotelResponseDTO = hotelServiceClient.getHotelByHotelId(hotelId);
        } catch (FeignException e) {
            log.error("Hotel service unavailable or hotel not found for id={}", hotelId, e);
            throw new ResourceNotFoundException("Hotel not found with id=" + hotelId);
        }

        if (role.equals("ROLE_USER")) {
            return reviewRepository.findByHotelIdAndStatus(hotelId, ReviewStatus.ACTIVE).stream()
                    .map(ReviewMapper::toDTO)
                    .collect(Collectors.toList());
        } else if (reviewSecurity.isAdmin(authentication)) {
            return reviewRepository.findByHotelId(hotelId).stream()
                    .map(ReviewMapper::toDTO)
                    .collect(Collectors.toList());
        } else {
            if (Objects.equals(userName, hotelResponseDTO.getOwnerUsername())) {
                return reviewRepository.findByHotelId(hotelId).stream()
                        .map(ReviewMapper::toDTO)
                        .collect(Collectors.toList());
            } else {
                throw new AccessDeniedException("Not a valid Hotel Owner to access the status with hotelId: " + hotelId);
            }

        }
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByUserId(String userId) {
        log.info("Fetching reviews by userId: {}", userId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = reviewSecurity.getUserId(authentication);
        String role = reviewSecurity.getUserRole(authentication);

        // Validate user
        try {
            userServiceClient.getUserById(userId);
        } catch (FeignException e) {
            log.error("User service unavailable or user not found for id={}", userId, e);
            throw new ResourceNotFoundException("User not found with id=" + userId);
        }

        if (reviewSecurity.isAdmin(authentication)) {
            return reviewRepository.findByUserId(userId).stream()
                    .map(ReviewMapper::toDTO)
                    .collect(Collectors.toList());
        }
        if (role.equals("ROLE_USER")) {
            reviewSecurity.validateOwnershipOrAdmin(userId, authentication);
            return reviewRepository.findByUserIdAndStatus(userId, ReviewStatus.ACTIVE).stream()
                    .map(ReviewMapper::toDTO)
                    .toList();
        }

        throw new AccessDeniedException("Access denied");
    }

    @Override
    public ReviewResponseDTO updateReviewComment(String reviewId, UpdateReviewCommentDTO requestDTO) {

        log.info("Updating reviewId={} with request={}", reviewId, requestDTO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Review review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with reviewId: " + reviewId));

        // Allow only if ADMIN, or review owner
        reviewSecurity.validateOwnershipOrAdmin(review.getUserId(), authentication);

        review.setComment(requestDTO.getComment());
        Review updatedReview = reviewRepository.save(review);

        log.info("Review comment updated successfully with reviewId={}", updatedReview.getReviewId());

        return ReviewMapper.toDTO(updatedReview);
    }

    @Override
    public ResponseDTO deleteReview(String reviewId) {
        log.info("Deleting review with reviewId = {}", reviewId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!reviewSecurity.isAdmin(authentication)) {
            log.warn("Only ADMIN can Delete a Review!");
            throw new AccessDeniedException("Only ADMIN can Delete a Review!");
        }

        Review review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with reviewId: " + reviewId));

        review.setStatus(ReviewStatus.DELETED);
        reviewRepository.save(review);
        log.info("Review deleted successfully with reviewId = {}", reviewId);

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.toString())
                .statusMessage("Review deleted successfully with reviewId = " + reviewId)
                .build();
    }

    @Override
    public Double getAverageRatingByHotelId(String hotelId) {
        log.info("Fetching average rating for hotelId={}", hotelId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = reviewSecurity.getUserId(authentication);
        String role = reviewSecurity.getUserRole(authentication);

        // Validate hotelId -  using Feign Client
        HotelResponseDTO hotelResponseDTO;
        try {
            hotelResponseDTO = hotelServiceClient.getHotelByHotelId(hotelId);
        } catch (FeignException e) {
            log.error("Hotel service unavailable or hotel not found for id={}", hotelId, e);
            throw new ResourceNotFoundException("Hotel not found with id=" + hotelId);
        }


        if (role.equals("ROLE_HOTEL_MANAGER")) {
            // HOTEL_MANAGER → must own the hotel
            if (!Objects.equals(userName, hotelResponseDTO.getOwnerUsername())) {
                log.warn("Unauthorized access attempt by user={} for hotelId={}", userName, hotelId);
                throw new AccessDeniedException("Not owner of hotelId=" + hotelId);
            }
        }

        Double avgRating = hotelResponseDTO.getRating();

        if (avgRating == null) {
            log.info("No reviews found for hotelId={}. Returning 0.0", hotelId);
            return 0.0;
        }

        log.info("Average rating for hotelId={} is {}", hotelId, avgRating);
        return avgRating;
    }

    @Override
    public UserIdResponseDTO getUserIdByReviewId_internal(String reviewId) {
        log.info("[Internal]: Fetching review by reviewId = {}", reviewId);

        Review review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with reviewId: " + reviewId));

        return UserIdResponseDTO.builder()
                .userId(review.getUserId())
                .message("UserId: " + review.getUserId() + " fetched successfully for reviewId: " + review.getReviewId())
                .build();
    }

    @Override
    public List<HotelReviewResponseDTO> getReviewsByUserId_internal(String userId) {
        // Return empty list when none found (safer for internal calls)
        List<Review> reviews = reviewRepository.findByUserId(userId);

        return reviews.stream()
                .map(review -> HotelReviewResponseDTO.builder()
                        .reviewId(review.getReviewId())
                        .userId(review.getUserId())
                        .hotelId(review.getHotelId())
                        .comment(review.getComment())
                        .rating(review.getRating())
                        .build())
                .toList();
    }

    @Override
    public String getHotelIdByReviewId_internal(String reviewId) {
        log.info("Fetching hotelId by reviewId = {}", reviewId);

        Review review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with reviewId: " + reviewId));

        return review.getHotelId();
    }

    @Override
    public UpdateReviewStatusResponseDTO updateReviewStatus_internal(UpdateReviewStatusRequestDTO requestDTO) {
        Review review = reviewRepository.findByReviewId(requestDTO.getReviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Review Not Found with reviewId: " + requestDTO.getReviewId()));

        review.setStatus(requestDTO.getStatus());
        Review saved = reviewRepository.save(review);

        return UpdateReviewStatusResponseDTO.builder()
                .reviewId(saved.getReviewId())
                .status(saved.getStatus())
                .message("Review status updated successfully")
                .build();
    }

}
