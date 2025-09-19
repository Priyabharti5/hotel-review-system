package com.priya.hotelservice.service.impl;

import com.priya.hotelservice.client.ReviewServiceClient;
import com.priya.hotelservice.dto.*;
import com.priya.hotelservice.entity.Hotel;
import com.priya.hotelservice.enums.HotelStatus;
import com.priya.hotelservice.enums.RatingOperator;
import com.priya.hotelservice.exception.ResourceAlreadyExistsException;
import com.priya.hotelservice.exception.ResourceNotFoundException;
import com.priya.hotelservice.mapper.HotelMapper;
import com.priya.hotelservice.repository.HotelRepository;
import com.priya.hotelservice.security.HotelSecurity;
import com.priya.hotelservice.service.HotelService;
import com.priya.hotelservice.utils.HotelIdGeneratorUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Production-grade implementation of {@link HotelService}.
 * <p>
 * Handles all hotel-related business logic including creation, updates,
 * search, retrieval, and status management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ReviewServiceClient reviewServiceClient;
    private final HotelSecurity hotelSecurity;

    @Override
    public HotelResponseDTO registerHotel(HotelRequestDTO requestDTO, Authentication authentication) {

        String currentUser = authentication.getName();
        String role = hotelSecurity.getUserRole(authentication);

        String ownerId = (role.equals("ROLE_ADMIN")) ? requestDTO.getOwnerUsername() : authentication.getName();
        hotelSecurity.validateHotelOwner(ownerId);

        log.info("Creating new hotel with name: {} by user: {} with role: {}", requestDTO.getName(), currentUser, role);

        // Check for duplicate name
        if (hotelRepository.existsByNameIgnoreCase(requestDTO.getName())) {
            log.error("Hotel creation failed: Name '{}' already exists", requestDTO.getName());
            throw new ResourceAlreadyExistsException("Hotel name already exists: " + requestDTO.getName());
        }

        Hotel hotel = HotelMapper.toEntity(requestDTO);

        // Generate unique ID
        assert hotel != null;
        hotel.setHotelId(HotelIdGeneratorUtil.generateHotelId());

        // Owner assignment
        if (role.equals("ROLE_HOTEL_MANAGER")) {
            log.info("Hotel manager must not provide ownerUsername. It is set automatically.");
            if (requestDTO.getOwnerUsername() != null) {
                throw new AccessDeniedException("Hotel manager must not provide ownerUsername. It is set automatically.");
            }
            hotel.setOwnerUsername(authentication.getName());

        } else if (role.equals("ROLE_ADMIN")) {
            log.info("Admin must provide ownerUsername in the request.");
            String ownerFromRequest = requestDTO.getOwnerUsername();
            if (requestDTO.getOwnerUsername() == null) {
                throw new AccessDeniedException("Admin must provide ownerUsername in the request.");
            }
            hotel.setOwnerUsername(ownerFromRequest);

        } else {
            throw new AccessDeniedException("Only Hotel Managers or Admins can create hotels.");
        }

        hotel.setRating(0.0);
        hotel.setStatus(HotelStatus.ACTIVE);
        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created successfully with hotelId: {}", savedHotel.getHotelId());

        return HotelMapper.toDTO(savedHotel);
    }


    @Override
    @Transactional(readOnly = true)
    public HotelResponseDTO getHotelByHotelId(String hotelId, Authentication authentication) {
        log.info("Fetching hotel with hotelId: [{}]", hotelId);
        Hotel hotel = hotelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> {
                    log.error("Hotel not found with hotelId= [{}]", hotelId);
                    return new ResourceNotFoundException("Hotel not found with hotelId: " + hotelId);
                });
        hotelSecurity.validateOwnershipOrAdmin(hotel.getOwnerUsername(), authentication);
        return HotelMapper.toDTO(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelResponseDTO getHotelByHotelId_internal(String hotelId, Authentication authentication) {
        log.info("[Internal]: Fetching hotel with hotelId: {}", hotelId);
        Hotel hotel = hotelRepository.findByHotelId(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with hotelId: " + hotelId));
        return HotelMapper.toDTO(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getAllHotels(Authentication authentication) {
        log.info("Fetching all hotels for user: {}", authentication.getName());

        String role = hotelSecurity.getUserRole(authentication);
        List<Hotel> hotels = switch (role) {
            case "ROLE_ADMIN" -> hotelRepository.findAll();
            case "ROLE_USER" -> hotelRepository.findByStatus(HotelStatus.ACTIVE);
            default -> hotelRepository.findByOwnerUsername(hotelSecurity.getUserId(authentication));
        };

        if (hotels.isEmpty()) {
            log.warn("No hotels found for user={}", authentication.getName());
            throw new ResourceNotFoundException("No hotels found");
        }

        return hotels.stream()
                .map(HotelMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> searchHotelsByLocation(String location, Authentication authentication) {
        log.info("Searching hotels in location: {} for user: {}", location, authentication.getName());

        String role = hotelSecurity.getUserRole(authentication);
        List<Hotel> hotels = switch (role) {
            case "ROLE_ADMIN" -> hotelRepository.findByLocationContainingIgnoreCase(location);
            case "ROLE_USER" ->
                    hotelRepository.findByLocationContainingIgnoreCaseAndStatus(location, HotelStatus.ACTIVE);
            default ->
                    hotelRepository.findByOwnerUsernameAndLocationContainingIgnoreCase(hotelSecurity.getUserId(authentication), location);
        };

        if (hotels.isEmpty()) {
            log.warn("No hotels found with Location [{}] for location = {}", location, authentication.getName());
            throw new ResourceNotFoundException("No hotels found with name: " + location);
        }

        return hotels.stream()
                .map(HotelMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> searchHotelsByName(String name, Authentication authentication) {
        log.info("Searching hotels with name containing: {} for user: {}", name, authentication.getName());

        String role = hotelSecurity.getUserRole(authentication);
        List<Hotel> hotels = switch (role) {
            case "ROLE_ADMIN" -> hotelRepository.findByNameContainingIgnoreCase(name);
            case "ROLE_USER" -> hotelRepository.findByNameContainingIgnoreCaseAndStatus(name, HotelStatus.ACTIVE);
            default -> hotelRepository.findByOwnerUsernameAndNameContainingIgnoreCase(hotelSecurity.getUserId(authentication), name);
        };

        if (hotels.isEmpty()) {
            log.warn("No hotels found with name={} for user={}", name, authentication.getName());
            throw new ResourceNotFoundException("No hotels found with partial name: " + name);
        }

        return hotels.stream()
                .map(HotelMapper::toDTO)
                .toList();
    }


    @Override
    public HotelResponseDTO updateHotel(String hotelId, HotelRequestDTO requestDTO, Authentication authentication) {
        log.info("Updating hotel with hotelId: {}", hotelId);

        Hotel existingHotel = hotelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> {
                    log.error("Hotel not found with hotelId: [{}]", hotelId);
                    return new ResourceNotFoundException("Hotel not found with hotelId: " + hotelId);
                });

        hotelSecurity.validateOwnershipOrAdmin(existingHotel.getOwnerUsername(), authentication);
        // Check for name conflict if name is updated
        if (!existingHotel.getName().equalsIgnoreCase(requestDTO.getName()) &&
                hotelRepository.existsByNameIgnoreCase(requestDTO.getName())) {
            log.error("Hotel update failed: Name '{}' already exists", requestDTO.getName());
            throw new ResourceAlreadyExistsException("Hotel name already exists: " + requestDTO.getName());
        }

        // Update fields
        existingHotel.setName(requestDTO.getName());
        existingHotel.setLocation(requestDTO.getLocation());
        existingHotel.setAbout(requestDTO.getAbout());

        Hotel updatedHotel = hotelRepository.save(existingHotel);

        log.info("Hotel updated successfully with hotelId: {}", updatedHotel.getHotelId());
        return HotelMapper.toDTO(updatedHotel);
    }

    @Override
    public HotelResponseDTO updateHotelAvgRating_internal(String hotelId, Double averageRating) {
        log.info("[Internal]: Updating hotel avg rating with hotelId: {}", hotelId);

        Hotel existingHotel = hotelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> {
                    log.error("[Internal]: Hotel not found with hotelId: {}", hotelId);
                    return new ResourceNotFoundException("[Internal]: Hotel not found with hotelId: " + hotelId);
                });

        existingHotel.setRating(averageRating);

        Hotel updatedHotel = hotelRepository.save(existingHotel);

        log.info("[Internal]: Hotel avg rating updated successfully with hotelId: {} current rating: {}", updatedHotel.getHotelId(), updatedHotel.getRating());
        return HotelMapper.toDTO(updatedHotel);
    }

    @Override
    public void deleteHotel(String hotelId, Authentication authentication) {
        log.info("Deleting hotel with hotelId: {}", hotelId);

        Hotel hotel = hotelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> {
                    log.error("Hotel not found with hotelId: {}", hotelId);
                    return new ResourceNotFoundException("Hotel not found with hotelId: " + hotelId);
                });

        hotelSecurity.validateOwnershipOrAdmin(hotel.getOwnerUsername(), authentication);

        if (hotel.getStatus() == HotelStatus.DELETED) {
            log.warn("Hotel already marked as DELETED: hotelId={}", hotelId);
            return;
        }

        hotel.setStatus(HotelStatus.DELETED);
        hotelRepository.save(hotel);

        log.info("Hotel deleted successfully with hotelId: {}", hotelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getHotelsByOwner(String ownerId, Authentication authentication) {

        hotelSecurity.validateHotelOwner(ownerId);
        hotelSecurity.validateOwnershipOrAdmin(ownerId, authentication);

        List<Hotel> hotels = hotelRepository.findByOwnerUsername(ownerId);

        if (hotels.isEmpty()) {
            log.warn("No hotels found with ownerUserName: [{}]", ownerId);
            throw new ResourceNotFoundException("No hotels found for owner: " + ownerId);
        }

        log.info("Fetched [{}] hotels for owner: [{}]", hotels.size(), ownerId);
        return hotels.stream()
                .map(HotelMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getHotelsByOwnerAndLocation(String ownerId, String location, Authentication authentication) {

        hotelSecurity.validateHotelOwner(ownerId);
        hotelSecurity.validateOwnershipOrAdmin(ownerId, authentication);

        List<Hotel> hotels = hotelRepository.findByOwnerUsernameAndLocationContainingIgnoreCase(ownerId, location);

        if (hotels.isEmpty()) {
            log.warn("No hotels found with owner: [{}] & location: [{}]", ownerId, location);
            throw new ResourceNotFoundException("No hotels found for owner: " + ownerId + " & location: " + location);
        }

        log.info("Fetched [{}] hotels for owner: [{}] with location containing '{}'", hotels.size(), ownerId, location);
        return hotels.stream()
                .map(HotelMapper::toDTO)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getHotelsByOwnerAndHotelName(String ownerId, String hotelName, Authentication authentication) {
        hotelSecurity.validateHotelOwner(ownerId);
        hotelSecurity.validateOwnershipOrAdmin(ownerId, authentication);
        List<Hotel> hotels = hotelRepository.findByOwnerUsernameAndNameContainingIgnoreCase(ownerId, hotelName);

        if (hotels.isEmpty()) {
            log.warn("No hotels found with owner: [{}] & hotel name: [{}]", ownerId, hotelName);
            throw new ResourceNotFoundException("No hotels found for owner: " + ownerId + " & hotel Name: " + hotelName);
        }

        log.info("Fetched [{}] hotels for owner: [{}] with hotel name containing '{}'", hotels.size(), ownerId, hotelName);
        return hotels.stream()
                .map(HotelMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getHotelsByRating(RatingOperator operator, double ratingValue, Authentication authentication) {
        log.info("Fetching hotels by rating {} {} for user: {}", operator, ratingValue, authentication.getName());

        String role = hotelSecurity.getUserRole(authentication);
        List<Hotel> hotels;

        if (hotelSecurity.isAdmin(authentication)) {
            hotels = switch (operator) {
                case GREATER_THAN -> hotelRepository.findByRatingGreaterThan(ratingValue);
                case LESS_THAN -> hotelRepository.findByRatingLessThan(ratingValue);
                case EQUAL -> hotelRepository.findByRating(ratingValue);
            };
        } else if (role.equals("ROLE_USER")) {
            hotels = switch (operator) {
                case GREATER_THAN -> hotelRepository.findByRatingGreaterThanAndStatus(ratingValue, HotelStatus.ACTIVE);
                case LESS_THAN -> hotelRepository.findByRatingLessThanAndStatus(ratingValue, HotelStatus.ACTIVE);
                case EQUAL -> hotelRepository.findByRatingAndStatus(ratingValue, HotelStatus.ACTIVE);
            };
        } else {  // Hotel manager
            String ownerId = hotelSecurity.getUserId(authentication);
            hotels = switch (operator) {
                case GREATER_THAN -> hotelRepository.findByOwnerUsernameAndRatingGreaterThan(ownerId, ratingValue);
                case LESS_THAN -> hotelRepository.findByOwnerUsernameAndRatingLessThan(ownerId, ratingValue);
                case EQUAL -> hotelRepository.findByOwnerUsernameAndRating(ownerId, ratingValue);
            };
        }

        if (hotels.isEmpty()) {
            log.warn("No hotels found with rating: [{}]", ratingValue);
            throw new ResourceNotFoundException("No hotels found with ratings: " + ratingValue);
        }

        return hotels.stream()
                .map(HotelMapper::toDTO)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getHotelsByLocationAndMinRating(String location, double minRating, Authentication authentication) {
        log.info("Fetching hotels by location={} and minRating={} for user: {}", location, minRating, authentication.getName());

        String role = hotelSecurity.getUserRole(authentication);

        List<Hotel> hotels = switch (role) {
            case "ROLE_ADMIN" ->
                    hotelRepository.findByLocationContainingIgnoreCaseAndRatingGreaterThanEqual(location, minRating);
            case "ROLE_USER" ->
                    hotelRepository.findByLocationContainingIgnoreCaseAndRatingGreaterThanEqualAndStatus(location, minRating, HotelStatus.ACTIVE);
            default -> { // ROLE_HOTEL_MANAGER
                String ownerId = hotelSecurity.getUserId(authentication);
                yield hotelRepository.findByOwnerUsernameAndLocationContainingIgnoreCaseAndRatingGreaterThanEqual(ownerId, location, minRating);
            }
        };

        if (hotels.isEmpty()) {
            log.warn("No hotels found with location: [{}] & minRating: [{}]", location, minRating);
            throw new ResourceNotFoundException("No hotels found with location: " + location + " & minRating: " + minRating);
        }

        return hotels.stream()
                .map(HotelMapper::toDTO)
                .toList();
    }

    @Override
    public UpdateHotelStatusResponseDTO updateHotelStatus_internal(@Valid UpdateHotelStatusRequestDTO requestDTO) {
        String hotelId = requestDTO.getHotelId();
        log.info("[Internal]: Updating hotelStatus with hotelId: {}", hotelId);

        Hotel hotel = hotelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with hotelId: " + hotelId));

        HotelStatus currentStatus = hotel.getStatus();

        if (currentStatus == requestDTO.getStatus()) {
            log.info("No update performed. Hotel: [{}] already has status: [{}]", hotel.getHotelId(), currentStatus);
            return UpdateHotelStatusResponseDTO.builder()
                    .hotelId(hotel.getHotelId())
                    .status(currentStatus)
                    .message("Hotel already has status: " + currentStatus)
                    .build();
        }

        hotel.setStatus(requestDTO.getStatus());
        Hotel savedHotel = hotelRepository.save(hotel);

        log.info("Hotel status updated successfully for hotelId={} to status={}", hotelId, savedHotel.getStatus());
        return UpdateHotelStatusResponseDTO.builder()
                .hotelId(savedHotel.getHotelId())
                .status(savedHotel.getStatus())
                .message("Hotel status updated successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getAllDeletedHotels(Authentication authentication) {
        String username = authentication.getName();
        log.info("Fetching all INACTIVE hotels for user={}", username);

        String role = hotelSecurity.getUserRole(authentication);
        List<Hotel> hotels;

        if (hotelSecurity.isAdmin(authentication)) {
            // Admin → see all inactive hotels
            hotels = hotelRepository.findByStatus(HotelStatus.DELETED);
        } else if (role.equals("ROLE_HOTEL_MANAGER")) {
            // Hotel Manager → see only their own inactive hotels
            String ownerId = hotelSecurity.getUserId(authentication);
            hotels = hotelRepository.findByOwnerUsernameAndStatus(ownerId, HotelStatus.DELETED);
        } else {
            log.warn("Unauthorized attempt by user={} to access inactive hotels", authentication.getName());
            throw new AccessDeniedException("You are not authorized to access inactive hotels");
        }

        if (hotels.isEmpty()) {
            log.warn("No inactive hotels found for user={}", authentication.getName());
            throw new ResourceNotFoundException("No inactive hotels found");
        }

        return hotels.stream()
                .map(HotelMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getHotelsReviewedByUser(String userId, Authentication authentication) {
        String requester = authentication.getName();
        log.info("Fetching hotels reviewed by userId={} for requester={}", userId, requester);

        // Security check
        if (!hotelSecurity.isAdmin(authentication) && !authentication.getName().equals(userId)) {
            log.warn("Unauthorized access attempt: requester={} tried to access reviews of userId={}",
                    authentication.getName(), userId);
            throw new AccessDeniedException("You are not authorized to view hotels reviewed by this user");
        }

        List<HotelReviewResponseDTO> userReviews = reviewServiceClient.getReviewsByUserId(userId);

        if (userReviews.isEmpty()) {
            log.warn("No reviews found for userId={}", userId);
            throw new ResourceNotFoundException("No reviews found for user: " + userId);
        }

        List<String> hotelIds = userReviews.stream()
                .map(HotelReviewResponseDTO::getHotelId)
                .distinct()
                .toList();

        List<Hotel> hotels = hotelRepository.findByHotelIdIn(hotelIds);

        if (hotels.isEmpty()) {
            log.warn("No hotels found for reviewed hotelIds={}", hotelIds);
            throw new ResourceNotFoundException("No hotels found for user reviews");
        }

        return hotels.stream()
                .map(HotelMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HotelResponseDTO getHotelByReviewId(String reviewId) {
        log.info("Fetching hotel for reviewId={}", reviewId);
        String hotelIdByReviewId = reviewServiceClient.getHotelIdByReviewId(reviewId);

        Hotel hotel = hotelRepository.findByHotelId(hotelIdByReviewId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with hotelId: " + hotelIdByReviewId));

        return HotelMapper.toDTO(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllHotelIdsByHotelOwnerUserName_internal(String ownerUserName) {
        log.info("[Internal] Fetching all hotel IDs for ownerUserName={}", ownerUserName);

        List<Hotel> hotels = hotelRepository.findByOwnerUsername(ownerUserName);

        if (hotels.isEmpty()) {
            log.warn("[Internal] No hotels found for ownerUserName={}", ownerUserName);
            throw new ResourceNotFoundException("No hotels found for owner: " + ownerUserName);
        }

        return hotels.stream()
                .map(Hotel::getHotelId)
                .toList();
    }

}
