package com.priya.hotelservice.repository;

import com.priya.hotelservice.entity.Hotel;
import com.priya.hotelservice.enums.HotelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Hotel} entity.
 * <p>
 * Provides CRUD operations and custom queries for hotels.
 * All methods follow Spring Data JPA conventions and support case-insensitive and partial matching where applicable.
 * <p>
 * Note: Logging should be done at the service layer for method calls to trace business logic execution.
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    /**
     * Find a hotel by its unique business identifier {@code hotelId}.
     *
     * @param hotelId Unique hotel identifier
     * @return Optional containing Hotel if found
     */
    Optional<Hotel> findByHotelId(String hotelId);

    /**
     * Find a hotel by its unique ID and owner username.
     *
     * @param hotelId       Hotel identifier
     * @param ownerUsername Owner's username
     * @return Optional containing Hotel if found
     */
    Optional<Hotel> findByHotelIdAndOwnerUsername(String hotelId, String ownerUsername);

    /**
     * Find a hotel by ID and status.
     *
     * @param hotelId Hotel identifier
     * @param status  HotelStatus enum
     * @return Optional containing Hotel if found
     */
    Optional<Hotel> findByHotelIdAndStatus(String hotelId, HotelStatus status);

    /**
     * Check if a hotel with the given name already exists (case-insensitive).
     *
     * @param name Hotel name
     * @return true if exists
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find hotels by exact name (case-insensitive).
     *
     * @param name Hotel name
     * @return List of matching hotels
     */
    List<Hotel> findByNameIgnoreCase(String name);

    /**
     * Find hotels where name contains a substring (case-insensitive).
     *
     * @param name Substring to search
     * @return List of matching hotels
     */
    List<Hotel> findByNameContainingIgnoreCase(String name);

    /**
     * Find hotels by location substring (case-insensitive).
     *
     * @param location Partial location string
     * @return List of hotels at locations containing the string
     */
    List<Hotel> findByLocationContainingIgnoreCase(String location);

    /**
     * Find all hotels owned by a specific user.
     *
     * @param ownerUsername Owner's username
     * @return List of hotels
     */
    List<Hotel> findByOwnerUsername(String ownerUsername);

    /**
     * Find hotels owned by a user filtered by location (partial match, case-insensitive).
     *
     * @param ownerUsername Owner's username
     * @param location      Partial location
     * @return List of hotels
     */
    List<Hotel> findByOwnerUsernameAndLocationContainingIgnoreCase(String ownerUsername, String location);

    /**
     * Find hotels owned by a user filtered by hotel name (partial match, case-insensitive).
     *
     * @param ownerUsername Owner's username
     * @param name          Partial hotel name
     * @return List of hotels
     */
    List<Hotel> findByOwnerUsernameAndNameContainingIgnoreCase(String ownerUsername, String name);

    /**
     * Find hotels with rating greater than the given value.
     *
     * @param rating Minimum rating (exclusive)
     * @return List of hotels
     */
    List<Hotel> findByRatingGreaterThan(Double rating);

    /**
     * Find hotels with rating less than the given value.
     *
     * @param rating Maximum rating (exclusive)
     * @return List of matching hotels
     */
    List<Hotel> findByRatingLessThan(Double rating);

    /**
     * Find hotels with exact rating.
     *
     * @param rating Rating value
     * @return List of hotels with this rating
     */
    List<Hotel> findByRating(Double rating);

    /**
     * Find hotels in a location with minimum rating.
     *
     * @param location  Partial location string
     * @param minRating Minimum rating (inclusive)
     * @return List of matching hotels
     */
    List<Hotel> findByLocationContainingIgnoreCaseAndRatingGreaterThanEqual(String location, double minRating);

    /**
     * Find hotels owned by a user with rating greater than specified.
     *
     * @param ownerId Owner username
     * @param rating  Minimum rating (exclusive)
     * @return List of hotels
     */
    List<Hotel> findByOwnerUsernameAndRatingGreaterThan(String ownerId, double rating);

    /**
     * Find hotels owned by a user with rating less than specified.
     *
     * @param ownerId Owner username
     * @param rating  Maximum rating (exclusive)
     * @return List of hotels
     */
    List<Hotel> findByOwnerUsernameAndRatingLessThan(String ownerId, double rating);

    /**
     * Find hotels owned by a user with exact rating.
     *
     * @param ownerId Owner username
     * @param rating  Rating value
     * @return List of hotels
     */
    List<Hotel> findByOwnerUsernameAndRating(String ownerId, double rating);

    /**
     * Find hotels owned by a user in a specific location with minimum rating.
     *
     * @param ownerId   Owner username
     * @param location  Partial location string
     * @param minRating Minimum rating (inclusive)
     * @return List of matching hotels
     */
    List<Hotel> findByOwnerUsernameAndLocationContainingIgnoreCaseAndRatingGreaterThanEqual(String ownerId, String location, double minRating);

    /**
     * Find hotels by {@link HotelStatus}.
     *
     * @param status HotelStatus
     * @return List of hotels with given status
     */
    List<Hotel> findByStatus(HotelStatus status);

    /**
     * Find hotels by name containing string and with a specific status.
     *
     * @param hotelName Partial hotel name
     * @param status    HotelStatus
     * @return List of hotels
     */
    List<Hotel> findByNameContainingIgnoreCaseAndStatus(String hotelName, HotelStatus status);

    /**
     * Find hotels by location containing string and with a specific status.
     *
     * @param location Partial location string
     * @param status   HotelStatus
     * @return List of hotels
     */
    List<Hotel> findByLocationContainingIgnoreCaseAndStatus(String location, HotelStatus status);

    /**
     * Find hotels by location containing string, minimum rating, and status.
     *
     * @param location  Partial location string
     * @param minRating Minimum rating (inclusive)
     * @param status    HotelStatus
     * @return List of hotels
     */
    List<Hotel> findByLocationContainingIgnoreCaseAndRatingGreaterThanEqualAndStatus(String location, double minRating, HotelStatus status);

    /**
     * Find hotels with rating greater than a value and specific status.
     *
     * @param rating Minimum rating (exclusive)
     * @param status HotelStatus
     * @return List of hotels
     */
    List<Hotel> findByRatingGreaterThanAndStatus(Double rating, HotelStatus status);

    /**
     * Find hotels with rating less than a value and specific status.
     *
     * @param rating Maximum rating (exclusive)
     * @param status HotelStatus
     * @return List of hotels
     */
    List<Hotel> findByRatingLessThanAndStatus(Double rating, HotelStatus status);

    /**
     * Find hotels with exact rating and specific status.
     *
     * @param rating Rating value
     * @param status HotelStatus
     * @return List of hotels
     */
    List<Hotel> findByRatingAndStatus(Double rating, HotelStatus status);

    /**
     * Find hotels owned by a user with specific status.
     *
     * @param ownerUsername Owner username
     * @param status        HotelStatus
     * @return List of hotels
     */
    List<Hotel> findByOwnerUsernameAndStatus(String ownerUsername, HotelStatus status);

    /**
     * Find hotels by a list of hotel IDs.
     *
     * @param hotelIds List of hotel identifiers
     * @return List of matching hotels
     */
    List<Hotel> findByHotelIdIn(List<String> hotelIds);


}
