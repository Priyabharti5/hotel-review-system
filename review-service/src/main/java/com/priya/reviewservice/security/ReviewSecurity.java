package com.priya.reviewservice.security;

import com.priya.reviewservice.dto.UserResponseDTO;
import com.priya.reviewservice.exception.ResourceNotFoundException;
import com.priya.reviewservice.repository.ReviewRepository;
import com.priya.reviewservice.client.UserServiceClient;
import com.priya.reviewservice.client.HotelServiceClient;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Security helper for Review entity-specific authorization and ownership checks.
 * <p>
 * This class centralizes validation logic for:
 * <ul>
 *     <li>Ownership verification of reviews</li>
 *     <li>Role-based access checks (ADMIN, USER, HOTEL_MANAGER)</li>
 *     <li>Remote user validation via User Service</li>
 * </ul>
 */
@Component("reviewSecurity")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class ReviewSecurity {

    private final ReviewRepository reviewRepository;
    private final UserServiceClient userServiceClient;
    private final HotelServiceClient hotelServiceClient;

    /**
     * Checks if the current user is the owner of a given review.
     */
    public boolean isReviewOwner(String reviewId, String currentUserId) {
        if (!StringUtils.hasText(reviewId) || !StringUtils.hasText(currentUserId)) {
            log.warn("Validation failed: reviewId or currentUserId is blank. reviewId='{}', currentUserId='{}'", reviewId, currentUserId);
            return false;
        }

        return reviewRepository.findByReviewId(reviewId)
                .map(review -> {
                    boolean isOwner = review.getUserId().equals(currentUserId);
                    log.debug("Review ownership check: reviewId='{}', expectedUserId='{}', actualUserId='{}', result={}",
                            reviewId, currentUserId, review.getUserId(), isOwner);
                    return isOwner;
                })
                .orElseGet(() -> {
                    log.info("Review not found for reviewId='{}'", reviewId);
                    return false;
                });
    }

    /**
     * Gets userId (from JWT) from Authentication object.
     */
    public String getUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication object or principal name is null");
            throw new IllegalStateException("Invalid authentication state");
        }
        return authentication.getName();
    }

    /**
     * Gets user's role from Authentication object.
     */
    public String getUserRole(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            log.error("Authentication or authorities are null");
            throw new IllegalStateException("Invalid authentication state");
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No role assigned for user={}", authentication.getName());
                    return new IllegalStateException("No role assigned to user");
                });
    }

    /**
     * Checks if the user has ADMIN role.
     */
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Validates ownership or ADMIN role.
     */
    public void validateOwnershipOrAdmin(String resourceOwnerId, Authentication authentication) {
        String userId = getUserId(authentication);
        if (!isAdmin(authentication) && !resourceOwnerId.equals(userId)) {
            log.warn("Access denied for user={} on resourceOwner={}", userId, resourceOwnerId);
            throw new AccessDeniedException("Access denied: not owner or admin");
        }
        log.debug("Ownership/Admin validation passed for user={} on resourceOwner={}", userId, resourceOwnerId);
    }

    /**
     * Validates review owner existence via User Service.
     * <p>
     * Throws AccessDeniedException if the user does not exist or if the user-service is unavailable.
     *
     * @param ownerId userId of the review owner
     */
    public void validateReviewOwner(String ownerId) {
        if (!StringUtils.hasText(ownerId)) {
            log.warn("Review owner validation failed: ownerId is null or blank");
            throw new AccessDeniedException("Invalid review owner: ownerId is required");
        }

        try {
            UserResponseDTO response = userServiceClient.getUserById(ownerId);

            if (response == null || !ownerId.equals(response.getUserId())) {
                String message = (response != null) ? "UserId mismatch" : "No response from user service";
                log.warn("Invalid review owner validation for ownerId={} - {}", ownerId, message);
                throw new AccessDeniedException("Invalid owner for review creation: " + message);
            }

            log.debug("Review owner validated successfully for ownerId={}", ownerId);

        } catch (Exception ex) {
            log.warn("User not found for ownerId={}", ownerId);
            throw new ResourceNotFoundException("User not found");
        }
    }

    /**
     * Validates hotel ownership for HOTEL_MANAGER.
     */
    public void validateHotelOwner(String hotelId, Authentication authentication) {
        String userName = getUserId(authentication);
        try {
            var hotel = hotelServiceClient.getHotelByHotelId(hotelId);
            if (getUserRole(authentication).equals("ROLE_HOTEL_MANAGER") &&
                    !userName.equals(hotel.getOwnerUsername())) {
                log.warn("Unauthorized access attempt by user={} for hotelId={}", userName, hotelId);
                throw new AccessDeniedException("Not owner of hotelId=" + hotelId);
            }
        } catch (Exception e) {
            log.error("Hotel service unavailable or hotel not found for id={}", hotelId, e);
            throw new AccessDeniedException("Hotel validation failed: " + e.getMessage());
        }
    }
}
