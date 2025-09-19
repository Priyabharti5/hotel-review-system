package com.priya.userservice.mapper;

import com.priya.userservice.dto.UserValidationResponseDTO;
import com.priya.userservice.entity.User;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper utility for converting User entity to various DTOs.
 */
@Hidden
@Slf4j
public class UserValidationMapper {

    // Private constructor to prevent instantiation
    private UserValidationMapper() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Maps a User entity to UserValidationResponseDTO.
     *
     * @param user            The User entity
     * @param message         Message describing validation result
     * @param validHotelOwner Boolean flag indicating if user is a valid hotel owner
     * @return UserValidationResponseDTO populated with user info and validation result
     */
    public static UserValidationResponseDTO toUserValidationResponse(User user, String message, boolean validHotelOwner) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        return UserValidationResponseDTO.builder()
                .username(user.getUserId())
                .userType(user.getUserType())
                .userStatus(user.getStatus())
                .message(message)
                .validHotelOwner(validHotelOwner)
                .build();
    }
}
