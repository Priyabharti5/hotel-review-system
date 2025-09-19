package com.priya.userservice.mapper;

import com.priya.userservice.dto.UserRequestDTO;
import com.priya.userservice.dto.UserResponseDTO;
import com.priya.userservice.entity.User;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Utility class for converting between {@link User} entities and their corresponding DTOs.
 * <p>
 * This class provides static mapping methods to ensure clean separation of layers and
 * avoid direct entity exposure in external layers (controllers, clients).
 * <p>
 * It is stateless and should not be instantiated.
 */
@Slf4j
@Hidden
public class UserMapper {

    // Private constructor to prevent instantiation
    private UserMapper() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Converts a {@link User} entity to a {@link UserResponseDTO}.
     *
     * @param user the {@link User} entity (must not be null)
     * @return the mapped {@link UserResponseDTO}
     * @throws NullPointerException if the given user is null
     */
    public static UserResponseDTO toDTO(User user) {
        Objects.requireNonNull(user, "User entity must not be null");
        log.debug("Mapping User entity to UserResponseDTO for userId: {}", user.getUserId());

        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .about(user.getAbout())
                .userStatus(user.getStatus())
                .userType(user.getUserType())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Converts a {@link UserRequestDTO} to a {@link User} entity.
     *
     * @param dto the {@link UserRequestDTO} (must not be null)
     * @return the mapped {@link User} entity
     * @throws NullPointerException if the given dto is null
     */
    public static User toEntity(UserRequestDTO dto) {

        Objects.requireNonNull(dto, "UserRequestDTO must not be null");
        log.debug("Mapping UserRequestDTO to User entity for name: {}", dto.getName());

        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .mobile(dto.getMobile())
                .status(dto.getUserStatus())
                .userType(dto.getUserType())
                .about(dto.getAbout())
                .build();
    }
}
