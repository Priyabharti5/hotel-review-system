package com.priya.userservice.service;

import com.priya.userservice.dto.*;
import org.springframework.security.core.Authentication;
import com.priya.userservice.enums.UserStatus;

import java.util.List;

/**
 * Service interface for managing users.
 * <p>
 * Declares all business operations for {@link com.priya.userservice.entity.User} entity.
 * Includes methods for user registration, retrieval, update, deletion, status management, and validation.
 * <p>
 * Logging and input validation should be performed in the implementing class.
 * All responses should be wrapped in DTOs to enforce consistent API contracts.
 */
public interface UserService {

    /**
     * Registers a new user.
     * Internal Endpoint
     * <p>
     * The {@link UserRequestDTO} must be validated before calling this method (e.g., non-null fields, email/mobile format).
     * Implementations should log the registration attempt and any validation failures.
     *
     * @param requestDTO input data for creating the user
     * @return created {@link UserResponseDTO} including generated userId
     */
    UserResponseDTO registerUser_internal(UserRequestDTO requestDTO);

    /**
     * Retrieves a user by their unique 10-digit userId.
     * <p>
     * Validation: userId must be non-null and exactly 10 digits.
     * Implementations should log the lookup and throw a {@link com.priya.userservice.exception.ResourceNotFoundException} if not found.
     *
     * @param userId 10-digit unique userId
     * @return {@link UserResponseDTO} representing the user
     */
    UserResponseDTO getUserByUserId(String userId);

    /**
     * Retrieves all users in the system.
     * <p>
     * Implementations should log the retrieval event.
     *
     * @return list of {@link UserResponseDTO} objects
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Updates an existing user identified by userId.
     * <p>
     * Validation: userId must exist; {@link UserRequestDTO} must be validated (non-null, correct formats).
     * Implementations should log the before-and-after state of the user.
     *
     * @param userId     userId of the user to update
     * @param requestDTO new data to update
     * @return updated {@link UserResponseDTO}
     */
    UserResponseDTO updateUser(String userId, UserRequestDTO requestDTO);

    /**
     * Deletes a user by userId (soft delete preferred for production-grade system).
     * <p>
     * Validation: userId must exist.
     * Implementations should log the delete action.
     *
     * @param userId userId of the user to delete
     */
    void deleteUser(String userId);

    /**
     * Retrieves a user by their email address.
     * <p>
     * Validation: email must be a valid email format.
     * Access control: ensure {@link Authentication} user is allowed to access this data (e.g., admin or self).
     * Implementations should log the access.
     *
     * @param email unique email of the user
     * @return {@link UserResponseDTO} for the user
     */
    UserResponseDTO getUserByEmail(String email);

    /**
     * Retrieves a user by their mobile number.
     * <p>
     * Validation: mobile must be 10 digits and numeric.
     * Access control: check authentication.
     * Implementations should log the access.
     *
     * @param mobile 10-digit unique mobile number
     * @return {@link UserResponseDTO} for the user
     */
    UserResponseDTO getUserByMobile(String mobile);

    /**
     * Updates the status of a user internally (e.g., by admin workflow).
     * <p>
     * Validation: status transitions must be allowed (no invalid changes, e.g., DELETED → ACTIVE unless restored).
     * Implementations should log current vs new status and return a DTO indicating the result.
     *
     * @param requestDTO {@link UpdateUserStatusRequestDTO} containing username and new status
     * @return {@link UpdateUserStatusResponseDTO} with updated status and metadata
     */
    UpdateUserStatusResponseDTO updateUserStatus_internal(UpdateUserStatusRequestDTO requestDTO);

    /**
     * Validates whether a user can be assigned as a hotel owner.
     * <p>
     * Checks may include account status, role, and business rules.
     *
     * @param userId userId to validate
     * @return {@link UserValidationResponseDTO} with validation results
     */
    UserValidationResponseDTO validateUserForHotelOwner(String userId);

    /**
     * Retrieves a user by the review they have created.
     *
     * @param reviewId ID of the review
     * @return {@link UserResponseDTO} of the review author
     */
    UserResponseDTO getUserByReviewId(String reviewId);

    /**
     * Retrieves all users with {@link UserStatus#ACTIVE}.
     *
     * @return list of active users
     */
    List<UserResponseDTO> getAllActiveUsers();

    /**
     * Retrieves all active hotel owners.
     *
     * @return list of active hotel owners
     */
    List<UserResponseDTO> getAllActiveHotelOwners();

    /**
     * Retrieves all active normal (non-owner) users.
     *
     * @return list of active normal users
     */
    List<UserResponseDTO> getAllActiveNormalUsers();

    /**
     * Retrieves all deleted users.
     *
     * @return list of users with {@link UserStatus#DELETED}
     */
    List<UserResponseDTO> getAllDeletedUsers();

    /**
     * Retrieves all deleted hotel owners.
     *
     * @return list of deleted hotel owners
     */
    List<UserResponseDTO> getAllDeletedHotelOwners();

    /**
     * Retrieves all deleted normal (non-owner) users.
     *
     * @return list of deleted normal users
     */
    List<UserResponseDTO> getAllDeletedNormalUsers();

}
