package com.priya.authservice.mapper;

import com.priya.authservice.dto.user_registration.UserRegistrationRequestDTO;
import com.priya.authservice.dto.user_registration.UserRegistrationResponseDTO;
import com.priya.authservice.dto.user_registration.UserRequestDTO;
import com.priya.authservice.dto.user_registration.UserResponseDTO;
import com.priya.authservice.entity.AuthUser;
import com.priya.authservice.enums.UserStatus;
import com.priya.authservice.enums.UserType;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Mapper class for handling transformations between:
 * <ul>
 *   <li>{@link UserRegistrationRequestDTO} → {@link UserRequestDTO}</li>
 *   <li>{@link UserResponseDTO} + {@link AuthUser} → {@link UserRegistrationResponseDTO}</li>
 * </ul>
 * <p>
 * This class ensures separation of concerns by keeping mapping logic
 * isolated from services and controllers.
 * </p>
 *
 * <p><b>Usage:</b> Typically used in the AuthService layer when:
 * <ul>
 *     <li>Receiving a registration request from clients</li>
 *     <li>Calling the UserService to create the business user profile</li>
 *     <li>Combining user details with authentication metadata for the response</li>
 * </ul>
 * </p>
 *
 * <p>
 * All methods are <code>static</code> and the class is non-instantiable.
 * </p>
 */
@Slf4j
@Hidden
public class UserRegistrationMapper {

    // Private constructor to prevent instantiation
    private UserRegistrationMapper() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Maps an AuthService registration request DTO into a UserService request DTO.
     *
     * @param request the registration request DTO (must not be {@code null})
     * @return mapped {@link UserRequestDTO}
     * @throws IllegalArgumentException if {@code request} is null
     */
    public static UserRequestDTO toUserServiceRequest(UserRegistrationRequestDTO request, UserStatus userStatus, UserType userType) {
        Objects.requireNonNull(request, "UserRegistrationRequestDTO must not be null");

        log.debug("Mapping UserRegistrationRequestDTO to UserRequestDTO for email={}", request.getEmail());

        return UserRequestDTO.builder()
                .name(request.getName())
                .mobile(request.getMobile())
                .email(request.getEmail())
                .userStatus(userStatus)
                .userType(userType)
                .about(request.getAbout())
                .build();
    }

    /**
     * Combines data from {@link UserResponseDTO} (User Service response)
     * and {@link AuthUser} (Auth Service entity) to build the final
     * {@link UserRegistrationResponseDTO}.
     *
     * @param userResponse the response DTO from UserService (must not be {@code null})
     * @param authUser     the authentication entity containing role & status (must not be {@code null})
     * @return mapped {@link UserRegistrationResponseDTO}
     * @throws IllegalArgumentException if either argument is null
     */
    public static UserRegistrationResponseDTO toRegistrationResponse(
            UserResponseDTO userResponse,
            AuthUser authUser) {

        Objects.requireNonNull(userResponse, "UserResponseDTO must not be null");
        Objects.requireNonNull(authUser, "AuthUser must not be null");

        log.debug("Mapping UserResponseDTO + AuthUser to UserRegistrationResponseDTO for userId={}",
                userResponse.getUserId());

        return UserRegistrationResponseDTO.builder()
                .userName(userResponse.getUserId())
                .name(userResponse.getName())
                .mobile(userResponse.getMobile())
                .email(userResponse.getEmail())
                .about(userResponse.getAbout())
                .status(authUser.getStatus())
                .role(authUser.getRole().getName())
                .createdAt(userResponse.getCreatedAt() != null ? userResponse.getCreatedAt() : LocalDateTime.now())
                .updatedAt(userResponse.getUpdatedAt() != null ? userResponse.getUpdatedAt() : LocalDateTime.now())
                .build();
    }
}
