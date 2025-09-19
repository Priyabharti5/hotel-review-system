package com.priya.hotelservice.controller;

import com.priya.hotelservice.dto.*;
import com.priya.hotelservice.enums.RatingOperator;
import com.priya.hotelservice.service.HotelService;
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
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing hotel-related operations.
 *
 * <p>Exposes external APIs for:
 * <ul>
 *   <li>Hotel CRUD (create, update, delete, fetch)</li>
 *   <li>Search hotels by name, location, owner, rating</li>
 *   <li>Special queries (inactive hotels, reviewed hotels)</li>
 * </ul>
 *
 * <p>Also contains <b>internal APIs</b> (prefixed with <code>/_internal</code>)
 * for inter-service communication with Review Service.
 *
 * <p>Authorization:
 * <ul>
 *   <li><b>ADMIN</b> → Full access</li>
 *   <li><b>HOTEL_MANAGER</b> → Restricted to owned hotels</li>
 *   <li><b>USER</b> → Read-only access (search, fetch)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Hotel APIs", description = "Operations related to Hotels")
public class HotelController {

    private final HotelService hotelService;

    /**
     * Registers a new hotel.
     * <p>Accessible only by HOTEL_MANAGER or ADMIN.</p>
     *
     * @param requestDTO     hotel creation details
     * @param authentication authenticated user context
     * @return created hotel details
     */
    @Operation(summary = "Register a new hotel (OWNER or ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hotel created successfully",
                    content = @Content(schema = @Schema(implementation = HotelResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or hotel already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/register")
    @PreAuthorize("hasRole('HOTEL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<HotelResponseDTO> registerHotel(
            @Valid @RequestBody HotelRequestDTO requestDTO,
            Authentication authentication) {
        log.info("API CALL: Register hotel request={}", requestDTO);
        HotelResponseDTO registeredHotel = hotelService.registerHotel(requestDTO, authentication);
        log.info("API SUCCESS: Created hotelId={}", registeredHotel.getHotelId());
        return ResponseEntity.status(201).body(registeredHotel);
    }

    /**
     * Get hotel by its ID.
     *
     * @param hotelId        hotel identifier
     * @param authentication authenticated user context
     * @return hotel details
     */
    @Operation(summary = "Get hotel by ID (Owner/Admin/Users)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel found",
                    content = @Content(schema = @Schema(implementation = HotelResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{hotelId}")
    @PreAuthorize("@hotelSecurity.isHotelOwner(#hotelId, authentication) or hasRole('ADMIN')")
    public ResponseEntity<HotelResponseDTO> getHotelById(
            @PathVariable("hotelId") String hotelId,
            Authentication authentication) {
        log.info("API CALL: Get hotel by hotelId={}", hotelId);
        HotelResponseDTO response = hotelService.getHotelByHotelId(hotelId, authentication);
        log.info("API SUCCESS: Fetched hotelId={}", response.getHotelId());
        return ResponseEntity.ok(response);
    }

    /**
     * Internal endpoint to fetch hotel by ID for inter-service use.
     */
    @Hidden
    @GetMapping("/_internal/review/{hotelId}")
    public ResponseEntity<HotelResponseDTO> getHotelById_internal(
            @PathVariable("hotelId") String hotelId,
            Authentication authentication) {
        log.info("[INTERNAL]: API CALL: Get hotel by hotelId={}", hotelId);
        HotelResponseDTO response = hotelService.getHotelByHotelId_internal(hotelId, authentication);
        log.info("[INTERNAL]: API SUCCESS: Fetched hotelId={}", response.getHotelId());
        return ResponseEntity.ok(response);
    }

    /**
     * Get all hotels.
     */
    @Operation(summary = "Get all hotels (Admins, Users, Hotel Managers)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of hotels fetched successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = HotelResponseDTO.class))))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER','HOTEL_MANAGER')")
    public ResponseEntity<List<HotelResponseDTO>> getAllHotels(Authentication authentication) {
        log.info("API CALL: Fetch all hotels");
        List<HotelResponseDTO> hotels = hotelService.getAllHotels(authentication);
        log.info("API SUCCESS: Fetched hotels = {}", hotels);
        return ResponseEntity.ok(hotels);
    }

    /**
     * Search hotels by partial name.
     */
    @Operation(summary = "Search hotels by partial name (Admins, Users, Hotel Managers)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotels found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = HotelResponseDTO.class))))
    })
    @GetMapping("/search/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN','USER','HOTEL_MANAGER')")
    public ResponseEntity<List<HotelResponseDTO>> searchHotelsByName(
            @PathVariable("name") String name,
            Authentication authentication) {
        log.info("API CALL: Search hotels by partial name={}", name);
        List<HotelResponseDTO> hotels = hotelService.searchHotelsByName(name, authentication);
        log.info("API SUCCESS: Found {} hotels matching name={}", hotels.size(), name);
        return ResponseEntity.ok(hotels);
    }

    /**
     * Search hotels by partial location.
     */
    @Operation(summary = "Search hotels by partial location (Admins, Users, Hotel Managers)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotels found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = HotelResponseDTO.class))))
    })
    @GetMapping("/search/location/{location}")
    @PreAuthorize("hasAnyRole('ADMIN','USER','HOTEL_MANAGER')")
    public ResponseEntity<List<HotelResponseDTO>> searchHotelsByLocation(
            @PathVariable("location") String location,
            Authentication authentication) {
        log.info("API CALL: Search hotels by location={}", location);
        List<HotelResponseDTO> hotels = hotelService.searchHotelsByLocation(location, authentication);
        log.info("API SUCCESS: Found {} hotels matching location={}", hotels.size(), location);
        return ResponseEntity.ok(hotels);
    }

    @Operation(summary = "Update hotel (Owner/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel updated successfully",
                    content = @Content(schema = @Schema(implementation = HotelResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{hotelId}")
    @PreAuthorize("@hotelSecurity.isHotelOwner(#hotelId, authentication) or hasRole('ADMIN')")
    public ResponseEntity<HotelResponseDTO> updateHotel(
            @PathVariable("hotelId") String hotelId,
            @Valid @RequestBody HotelRequestDTO requestDTO,
            Authentication authentication) {
        log.info("API CALL: Update hotelId={}, request={}", hotelId, requestDTO);
        HotelResponseDTO updatedHotel = hotelService.updateHotel(hotelId, requestDTO, authentication);
        log.info("API SUCCESS: Updated hotelId={}", updatedHotel.getHotelId());
        return ResponseEntity.ok(updatedHotel);
    }

    @Hidden
    @PutMapping("/_internal/review/rating/{hotelId}")
    public ResponseEntity<HotelResponseDTO> updateHotelAvgRating_internal(
            @PathVariable("hotelId") String hotelId,
            @RequestParam("averageRating") Double averageRating) {
        log.info("[[INTERNAL]: Updating average rating for hotelId={} to {}", hotelId, averageRating);
        HotelResponseDTO updatedHotel = hotelService.updateHotelAvgRating_internal(hotelId, averageRating);
        log.info("[INTERNAL]: API SUCCESS: Updated hotel avg rating for hotelId={}. Updated rating = {}", updatedHotel.getHotelId(), averageRating);
        return ResponseEntity.ok(updatedHotel);
    }

    @Operation(summary = "Delete a hotel by Hotel ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{hotelId}")
    @PreAuthorize("@hotelSecurity.isHotelOwner(#hotelId, authentication) or hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteHotel(
            @PathVariable("hotelId") String hotelId,
            Authentication authentication) {
        log.info("API CALL: Delete hotelId={}", hotelId);
        hotelService.deleteHotel(hotelId, authentication);
        log.info("API SUCCESS: Deleted hotelId={}", hotelId);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .statusCode("200")
                        .statusMessage("Hotel deleted successfully")
                        .build()
        );
    }

    /**
     * Get all hotels owned by a user
     *
     * @param userId         to find hotel
     * @param authentication logged in user
     * @return List of Hotels owned by given user
     */
    @Operation(summary = "Get all hotels owned by the given userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotels fetched successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = HotelResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "No hotels found for this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/owner/{userId}")
    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<List<HotelResponseDTO>> getHotelsByOwner(
            @Valid @PathVariable("userId") String userId,
            Authentication authentication) {
        log.info("API CALL: Get hotels for owner userId={}", userId);
        List<HotelResponseDTO> hotels = hotelService.getHotelsByOwner(userId, authentication);
        log.info("API SUCCESS: Found {} hotels for userId={}", hotels.size(), userId);
        return ResponseEntity.ok(hotels);
    }

    /**
     * Get all hotels owned by userId + filter by location
     *
     * @param userId         - user
     * @param location       - partial location
     * @param authentication - logged-in user
     * @return List of Hotels
     */
    @Operation(summary = "Get all hotels owned by the given userId in a specific location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotels fetched successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = HotelResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "No hotels found for this user and location",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/owner/{userId}/location/{location}")
    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<List<HotelResponseDTO>> getHotelsByOwnerAndLocation(
            @PathVariable("userId") String userId,
            @PathVariable("location") String location,
            Authentication authentication) {
        log.info("API CALL: Get hotels for owner userId={} and location={}", userId, location);
        List<HotelResponseDTO> hotels = hotelService.getHotelsByOwnerAndLocation(userId, location, authentication);
        log.info("API SUCCESS: Found {} hotels for userId={} and location={}", hotels.size(), userId, location);
        return ResponseEntity.ok(hotels);
    }

    /**
     * Get all hotels owned by userId + filter by partial hotel name
     *
     * @param userId         - User
     * @param name           - hotel name
     * @param authentication - logged in user
     * @return List of hotels
     */
    @Operation(summary = "Get all hotels owned by the given userId and matching partial hotel name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotels fetched successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = HotelResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "No hotels found for this user and hotel name",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/owner/{userId}/search/name/{name}")
    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<List<HotelResponseDTO>> getHotelsByOwnerAndName(
            @PathVariable("userId") String userId,
            @PathVariable("name") String name,
            Authentication authentication) {
        log.info("API CALL: Get hotels for owner userId={} and partial name={}", userId, name);
        List<HotelResponseDTO> hotels = hotelService.getHotelsByOwnerAndHotelName(userId, name, authentication);
        log.info("API SUCCESS: Found {} hotels for userId={} and name={}", hotels.size(), userId, name);
        return ResponseEntity.ok(hotels);
    }

    /**
     * Get all hotels whose rating is greater/less than given rating value
     *
     * @param operator       "gt" or "lt" or "eq"
     * @param value          rating value
     * @param authentication logged in user
     * @return list of hotels
     */
    @GetMapping("/rating/{operator}/{value}")
    @PreAuthorize("hasAnyRole('ADMIN','USER','HOTEL_MANAGER')")
    public ResponseEntity<List<HotelResponseDTO>> getHotelsByRating(
            @PathVariable("operator") String operator, //
            @PathVariable("value") double value,
            Authentication authentication) {
        log.info("API CALL: Get hotels by rating operator={} value={}", operator, value);
        RatingOperator op = RatingOperator.fromCode(operator);
        List<HotelResponseDTO> hotels = hotelService.getHotelsByRating(op, value, authentication);
        log.info("API SUCCESS: Found {} hotels with operator={} value={}", hotels.size(), operator, value);
        return ResponseEntity.ok((hotels));
    }

    /**
     * Get all hotels whose rating is like 4 or more for given location
     *
     * @param location       partial location
     * @param minRating      rating value in double
     * @param authentication - logged in user
     * @return list of hotels
     */
    @GetMapping("/location/{location}/rating/{minRating}")
    @PreAuthorize("hasAnyRole('ADMIN','USER','HOTEL_MANAGER')")
    public ResponseEntity<List<HotelResponseDTO>> getHotelsByLocationAndMinRating(
            @PathVariable("location") String location,
            @PathVariable("minRating") double minRating,
            Authentication authentication) {
        log.info("API CALL: Get hotels at location={} with minRating>={}", location, minRating);
        List<HotelResponseDTO> hotels = hotelService.getHotelsByLocationAndMinRating(location, minRating, authentication);
        log.info("API SUCCESS: Found {} hotels at location={} with minRating>={}", hotels.size(), location, minRating);
        return ResponseEntity.ok(hotels);
    }

    @Hidden
    @PutMapping("/_internal/hotel/status")
    public ResponseEntity<UpdateHotelStatusResponseDTO> updateHotelStatus_internal(
            @Valid @RequestBody UpdateHotelStatusRequestDTO requestDTO) {
        log.info("[INTERNAL]: API CALL: update hotel: [{}] status {}", requestDTO.getHotelId(), requestDTO.getStatus());
        UpdateHotelStatusResponseDTO responseDTO = hotelService.updateHotelStatus_internal(requestDTO);
        log.info("[INTERNAL]: API SUCCESS: Hotel: {} status updated: {}", responseDTO.getHotelId(), responseDTO.getStatus());
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasAnyRole('ADMIN','HOTEL_MANAGER')")
    @Operation(summary = "Get all inactive hotels",
            description = "Accessible only by Admins and Hotel Managers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of inactive hotels retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "No inactive hotels found")
    })
    public ResponseEntity<List<HotelResponseDTO>> getAllDeletedHotels(Authentication authentication) {
        log.info("API CALL: Get all inactive hotels by user={}", authentication.getName());
        List<HotelResponseDTO> response = hotelService.getAllDeletedHotels(authentication);
        log.info("API SUCCESS: Found {} inactive hotels", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reviewed/{userId}")
    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')")
    @Operation(summary = "Get list of hotels reviewed by a specific user",
            description = "Fetches unique hotels where the given user has submitted reviews. " +
                    "Accessible by ADMIN or the user himself.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotels retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "No hotels found for given user")
    })
    public ResponseEntity<List<HotelResponseDTO>> getHotelsReviewedByUser(
            @PathVariable("userId") String userId,
            Authentication authentication) {
        log.info("API CALL: Get hotels reviewed by userId={} requestedBy={}", userId, authentication.getName());
        List<HotelResponseDTO> response = hotelService.getHotelsReviewedByUser(userId, authentication);
        log.info("API SUCCESS: Found {} reviewed hotels by userId={}", response.size(), userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get hotel by reviewId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel found"),
            @ApiResponse(responseCode = "404", description = "Hotel not found for reviewId")
    })
    @GetMapping("/review/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponseDTO> getHotelByReviewId(@PathVariable("reviewId") String reviewId) {
        log.info("API CALL: Get hotel by reviewId: [{}]", reviewId);
        HotelResponseDTO response = hotelService.getHotelByReviewId(reviewId);
        return ResponseEntity.ok(response);
    }

    @Hidden
    @GetMapping("/_internal/review/owner/{ownerUserName}")
    public ResponseEntity<List<String>> getHotelIdsByOwnerUserName_internal(@PathVariable("ownerUserName") String ownerUserName) {
        log.info("[INTERNAL]: API CALL: Fetching Hotel's Id with OwnerUserName: {}", ownerUserName);
        List<String> hotelIds = hotelService.getAllHotelIdsByHotelOwnerUserName_internal(ownerUserName);
        log.info("[INTERNAL]: API SUCCESS: Fetched Hotel's Id : {} with OwnerUserName: {}", ownerUserName, hotelIds);
        return ResponseEntity.ok(hotelIds);
    }

}
