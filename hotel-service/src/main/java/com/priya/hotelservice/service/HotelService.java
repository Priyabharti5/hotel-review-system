package com.priya.hotelservice.service;

import com.priya.hotelservice.dto.HotelRequestDTO;
import com.priya.hotelservice.dto.HotelResponseDTO;
import com.priya.hotelservice.dto.UpdateHotelStatusRequestDTO;
import com.priya.hotelservice.dto.UpdateHotelStatusResponseDTO;
import com.priya.hotelservice.exception.ResourceNotFoundException;
import com.priya.hotelservice.enums.RatingOperator;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for managing hotels.
 *
 * <p>This interface defines all business operations related to the Hotel entity.
 * Implementations are responsible for:
 * <ul>
 *     <li>CRUD operations on hotels.</li>
 *     <li>Owner-based queries.</li>
 *     <li>Rating calculations and filtering.</li>
 *     <li>Status management for hotels.</li>
 * </ul>
 *
 * <p>Authentication is required for most operations to enforce owner-specific
 * access or admin privileges. Logging should be implemented in each method
 * to track API calls and outcomes.
 *
 * <p>All DTOs must be validated before persistence. {@code @Valid} annotations
 * are applied where appropriate.
 */
public interface HotelService {

    /**
     * Registers a new hotel.
     *
     * @param requestDTO     Validated DTO containing hotel details.
     * @param authentication Authentication object representing the current user.
     * @return HotelResponseDTO containing persisted hotel information.
     * @throws IllegalArgumentException if hotel data is invalid or duplicate.
     * @implNote Log API call with hotel name and owner, log success/failure.
     */
    HotelResponseDTO registerHotel(HotelRequestDTO requestDTO, Authentication authentication);

    /**
     * Retrieves hotel by its unique ID for the authenticated user.
     *
     * @param hotelId        Unique hotel identifier.
     * @param authentication Authentication object representing current user.
     * @return HotelResponseDTO containing hotel details.
     * @throws ResourceNotFoundException if hotel is not found.
     * @implNote Log hotelId lookup and outcome.
     */
    HotelResponseDTO getHotelByHotelId(String hotelId, Authentication authentication);

    /**
     * Internal retrieval of hotel by ID without additional access checks.
     *
     * @param hotelId        Unique hotel identifier.
     * @param authentication Current authentication context (for logging).
     * @return HotelResponseDTO containing hotel details.
     */
    HotelResponseDTO getHotelByHotelId_internal(String hotelId, Authentication authentication);

    /**
     * Updates an existing hotel.
     *
     * @param hotelId        ID of the hotel to update.
     * @param requestDTO     Validated DTO containing updated hotel details.
     * @param authentication Authentication context of the user performing update.
     * @return Updated HotelResponseDTO.
     * @throws ResourceNotFoundException if hotel does not exist.
     * @implNote Log old vs new data changes for audit purposes.
     */
    HotelResponseDTO updateHotel(String hotelId, HotelRequestDTO requestDTO, Authentication authentication);

    /**
     * Updates average rating of a hotel internally.
     *
     * @param hotelId       Unique hotel ID.
     * @param averageRating Computed average rating.
     * @return Updated HotelResponseDTO.
     */
    HotelResponseDTO updateHotelAvgRating_internal(String hotelId, Double averageRating);

    /**
     * Deletes a hotel owned by the authenticated user.
     *
     * @param hotelId        ID of the hotel to delete.
     * @param authentication Current user context.
     * @throws ResourceNotFoundException if hotel does not exist.
     * @implNote Log deletion attempts with username and hotelId.
     */
    void deleteHotel(String hotelId, Authentication authentication);

    /**
     * Retrieves all hotels owned by a specific user.
     *
     * @param ownerId        Owner's user ID.
     * @param authentication Current authentication context.
     * @return List of HotelResponseDTO owned by the user.
     */
    List<HotelResponseDTO> getHotelsByOwner(String ownerId, Authentication authentication);

    /**
     * Retrieves hotels owned by a user filtered by location.
     *
     * @param ownerId        Owner's user ID.
     * @param location       Hotel location to filter.
     * @param authentication Current authentication context.
     * @return List of HotelResponseDTO matching criteria.
     */
    List<HotelResponseDTO> getHotelsByOwnerAndLocation(String ownerId, String location, Authentication authentication);

    /**
     * Retrieves hotels owned by a user filtered by hotel name.
     *
     * @param ownerId        Owner's user ID.
     * @param name           Partial/full hotel name.
     * @param authentication Current authentication context.
     * @return List of HotelResponseDTO matching criteria.
     */
    List<HotelResponseDTO> getHotelsByOwnerAndHotelName(String ownerId, String name, Authentication authentication);

    /**
     * Searches hotels by partial name match.
     *
     * @param partialName    Partial hotel name.
     * @param authentication Current user context.
     * @return List of hotels matching the search term.
     */
    List<HotelResponseDTO> searchHotelsByName(String partialName, Authentication authentication);

    /**
     * Searches hotels by location.
     *
     * @param location       Location string.
     * @param authentication Current user context.
     * @return List of hotels matching location.
     */
    List<HotelResponseDTO> searchHotelsByLocation(String location, Authentication authentication);

    /**
     * Retrieves all hotels accessible to the user.
     *
     * @param authentication Current authentication context.
     * @return List of all HotelResponseDTO.
     */
    List<HotelResponseDTO> getAllHotels(Authentication authentication);

    /**
     * Retrieves hotels filtered by rating operator and value.
     *
     * @param operator       Rating operator (GREATER_THAN, LESS_THAN, EQUAL).
     * @param value          Threshold rating.
     * @param authentication Current authentication context.
     * @return List of hotels matching rating criteria.
     */
    List<HotelResponseDTO> getHotelsByRating(RatingOperator operator, double value, Authentication authentication);

    /**
     * Retrieves hotels at a specific location with minimum rating.
     *
     * @param location       Hotel location.
     * @param minRating      Minimum rating threshold.
     * @param authentication Current authentication context.
     * @return List of hotels matching criteria.
     */
    List<HotelResponseDTO> getHotelsByLocationAndMinRating(String location, double minRating, Authentication authentication);

    /**
     * Internal method to update hotel status.
     *
     * @param requestDTO Validated status update DTO.
     * @return Response DTO indicating success/failure.
     */
    UpdateHotelStatusResponseDTO updateHotelStatus_internal(@Valid UpdateHotelStatusRequestDTO requestDTO);

    /**
     * Retrieves all inactive hotels.
     *
     * @param authentication Current authentication context.
     * @return List of inactive hotels.
     */
    List<HotelResponseDTO> getAllDeletedHotels(Authentication authentication);

    /**
     * Retrieves hotels reviewed by a specific user.
     *
     * @param userId         ID of the user who submitted reviews.
     * @param authentication Current authentication context.
     * @return List of hotels reviewed by the user.
     */
    List<HotelResponseDTO> getHotelsReviewedByUser(String userId, Authentication authentication);

    /**
     * Retrieves a hotel based on a review ID.
     *
     * @param reviewId Review identifier.
     * @return HotelResponseDTO containing hotel details.
     */
    HotelResponseDTO getHotelByReviewId(String reviewId);

    /**
     * Returns a list of hotel IDs owned by a specific username.
     *
     * @param ownerUserName Owner's username.
     * @return List of hotel IDs.
     */
    List<String> getAllHotelIdsByHotelOwnerUserName_internal(String ownerUserName);

}
