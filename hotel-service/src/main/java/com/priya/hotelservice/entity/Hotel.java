package com.priya.hotelservice.entity;

import com.priya.hotelservice.enums.HotelStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a Hotel entity in the Hotel Review System.
 * Stores hotel details including name, location, and description.
 * <p>
 * NOTE:
 * - Relationships to User and Review are handled via service calls (Feign),
 * not direct JPA mappings, to keep microservices decoupled.
 * - Uses soft delete: when a deleted is issued, status is updated to DELETED.
 * - Deleted hotels are excluded from queries via Hibernate filter.
 */
@Entity
@Table(name = "hotels", uniqueConstraints = @UniqueConstraint(columnNames = "hotel_id"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Hotel extends BaseEntity {

    /**
     * Primary key of the hotel (internal use).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Unique business hotel ID (10-digit string).
     */
    @Column(name = "hotel_id", nullable = false, unique = true, length = 10, updatable = false)
    private String hotelId;

    /**
     * Name of the hotel.
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Location/address of the hotel.
     */
    @Column(name = "location", nullable = false, length = 200)
    private String location;

    /**
     * Optional description or details about the hotel.
     */
    @Column(name = "about", length = 500)
    private String about;


    /**
     * Overall average rating of the hotel (based on reviews).
     * Cached average rating (updated by review-service events).
     */
    @Column(name = "rating", nullable = false)
    private Double rating;

    /**
     * ID of the hotel manager (from auth-service).
     * This links the hotel to the owning user.
     */
    @Column(name = "owner_username", nullable = false, length = 10)
    private String ownerUsername;

    /**
     * Lifecycle status of the hotel.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HotelStatus status;


}
