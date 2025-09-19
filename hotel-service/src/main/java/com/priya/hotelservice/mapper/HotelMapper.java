package com.priya.hotelservice.mapper;

import com.priya.hotelservice.dto.HotelRequestDTO;
import com.priya.hotelservice.dto.HotelResponseDTO;
import com.priya.hotelservice.entity.Hotel;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper class for converting between {@link Hotel} entity and its DTOs.
 * <p>
 * This class provides static methods to convert {@link HotelRequestDTO} to {@link Hotel}
 * entity and {@link Hotel} entity to {@link HotelResponseDTO}.
 * <p>
 * Static methods are used to avoid object creation overhead.
 * Null inputs are handled gracefully with appropriate logging.
 */
@Slf4j
@Hidden
public class HotelMapper {

    // Private constructor to prevent instantiation
    private HotelMapper() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Converts {@link HotelRequestDTO} to {@link Hotel} entity.
     * <p>
     * Validates that the request DTO is not null. Logs a warning if null.
     *
     * @param requestDTO the Hotel request DTO, must not be null
     * @return the Hotel entity, or null if input is null
     */
    public static Hotel toEntity(HotelRequestDTO requestDTO) {
        if (requestDTO == null) {
            log.warn("HotelRequestDTO is null. Returning null entity.");
            return null;
        }

        return Hotel.builder()
                .name(requestDTO.getName())
                .location(requestDTO.getLocation())
                .about(requestDTO.getAbout())
                .build();
    }

    /**
     * Converts {@link Hotel} entity to {@link HotelResponseDTO}.
     * <p>
     * Validates that the Hotel entity is not null. Logs a warning if null.
     *
     * @param hotel the Hotel entity
     * @return the Hotel response DTO, or null if input is null
     */
    public static HotelResponseDTO toDTO(Hotel hotel) {
        if (hotel == null) {
            log.warn("Hotel entity is null. Returning null DTO.");
            return null;
        }

        return HotelResponseDTO.builder()
                .hotelId(hotel.getHotelId())
                .name(hotel.getName())
                .location(hotel.getLocation())
                .about(hotel.getAbout())
                .rating(hotel.getRating())
                .ownerUsername(hotel.getOwnerUsername())
                .status(hotel.getStatus())
                .createdAt(hotel.getCreatedAt())
                .updatedAt(hotel.getUpdatedAt())
                .build();
    }
}
