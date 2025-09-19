package com.priya.hotelservice.security;

import com.priya.hotelservice.dto.UserValidationResponseDTO;
import com.priya.hotelservice.exception.ResourceNotFoundException;
import com.priya.hotelservice.repository.HotelRepository;
import com.priya.hotelservice.client.UserServiceFeignClient;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Security helper for Hotel entity-specific authorization and ownership checks.
 * <p>
 * This class centralizes validation logic for:
 * <ul>
 *     <li>Ownership verification of hotels</li>
 *     <li>Role-based access checks (Admin, Manager, etc.)</li>
 *     <li>Remote user validation via User Service</li>
 * </ul>
 * Not directly exposed as an API endpoint.
 */
@Component("hotelSecurity")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class HotelSecurity {

    private final HotelRepository hotelRepository;
    private final UserServiceFeignClient userServiceFeignClient;

    /**
     * Checks if the authenticated user is the owner of a hotel.
     *
     * @param hotelId        hotel ID to check
     * @param authentication authentication object
     * @return true if owner, false otherwise
     */
    public boolean isHotelOwner(String hotelId, Authentication authentication) {
        if (hotelId == null) {
            log.warn("hotelId is null");
            return false;
        }
        if (authentication == null || authentication.getName() == null) {
            log.warn("Authentication or principal name is null for hotelId={}", hotelId);
            return false;
        }

        String currentUserId = authentication.getName();
        return hotelRepository.findByHotelId(hotelId)
                .map(hotel -> {
                    boolean isOwner = hotel.getOwnerUsername().equals(currentUserId);
                    log.debug("Ownership check for hotelId={} by user={} : {}", hotelId, currentUserId, isOwner);
                    return isOwner;
                })
                .orElseGet(() -> {
                    log.warn("Hotel not found for hotelId={} during ownership validation", hotelId);
                    return false;
                });
    }

    /**
     * Extracts userId (username) from the Authentication object.
     *
     * @param authentication authenticated principal
     * @return user identifier
     */
    public String getUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication object or principal name is null");
            throw new IllegalStateException("Invalid authentication state");
        }
        return authentication.getName();
    }

    /**
     * Extracts the role of the authenticated user.
     *
     * @param authentication authenticated principal
     * @return role string (e.g., ROLE_ADMIN, ROLE_HOTEL_MANAGER)
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
     * Checks whether the authenticated user has ADMIN role.
     *
     * @param authentication authentication object
     * @return true if ADMIN
     */
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Validates that the authenticated user is either the owner or an admin.
     *
     * @param resourceOwnerId resource owner's username
     * @param authentication  authentication object
     */
    public void validateOwnershipOrAdmin(String resourceOwnerId, Authentication authentication) {
        String currentUserId = authentication.getName();
        if (!isAdmin(authentication) && !resourceOwnerId.equals(currentUserId)) {
            log.warn("Access denied for user={} on resourceOwner={}", currentUserId, resourceOwnerId);
            throw new AccessDeniedException("Access denied: not owner or admin");
        }
        log.debug("Ownership/Admin validation passed for user={} on resourceOwner={}", currentUserId, resourceOwnerId);
    }

    /**
     * Validates hotel owner existence via User Service.
     *
     * @param ownerId owner username
     */
    public void validateHotelOwner(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            log.warn("Hotel owner validation failed: ownerId is null or blank");
            throw new AccessDeniedException("Invalid hotel owner: ownerId is required");
        }
        try {
            UserValidationResponseDTO response = userServiceFeignClient.validateHotelOwner(ownerId);
            if (response == null || !Boolean.TRUE.equals(response.getValidHotelOwner())) {
                String message = (response != null) ? response.getMessage() : "No response from user service";
                log.warn("Invalid hotel owner validation for ownerId={} - {}", ownerId, message);
                throw new AccessDeniedException("Invalid owner for hotel creation: " + message);
            }
            log.debug("Hotel owner validated successfully for ownerId={}", ownerId);
        } catch (Exception ex) {
            log.error("Hotel owner not found for ownerId={}: {}", ownerId, ex.getMessage(), ex);
            throw new ResourceNotFoundException("Hotel owner not found");
        }
    }
}