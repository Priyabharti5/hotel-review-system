package com.priya.userservice.service.impl;

import com.priya.userservice.client.AuthServiceFeignClient;
import com.priya.userservice.client.ReviewServiceClient;
import com.priya.userservice.dto.*;
import com.priya.userservice.entity.User;
import com.priya.userservice.enums.UserStatus;
import com.priya.userservice.enums.UserType;
import com.priya.userservice.exception.ResourceAlreadyExistsException;
import com.priya.userservice.exception.ResourceNotFoundException;
import com.priya.userservice.mapper.UserMapper;
import com.priya.userservice.mapper.UserValidationMapper;
import com.priya.userservice.repository.UserRepository;
import com.priya.userservice.security.UserSecurity;
import com.priya.userservice.service.UserService;
import com.priya.userservice.utils.UserIdGeneratorUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Production-grade service implementation for managing users.
 * <p>
 * Handles CRUD operations, status updates, validations, and external service integrations (AuthService, ReviewService).
 * Includes logging, validation, and security checks for method-level access.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthServiceFeignClient authServiceFeignClient;
    private final ReviewServiceClient reviewServiceClient;
    private final UserSecurity userSecurity;

    /**
     * Validates that a user is ACTIVE before performing operations.
     *
     * @param user The user to validate
     * @throws ResourceNotFoundException if user is not ACTIVE
     */
    private void validateUserIsActive(User user) {
        if (user.getStatus() == null || user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Operation blocked: User [{}] is not ACTIVE (status={})", user.getUserId(), user.getStatus());
            throw new ResourceNotFoundException("User is not active: " + user.getUserId());
        }
    }

    @Override
    public UserResponseDTO registerUser_internal(UserRequestDTO requestDTO) {
        log.info("Creating user with name: {}", requestDTO.getName());
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("User with email already exists: " + requestDTO.getEmail());
        }
        if (userRepository.existsByMobile(requestDTO.getMobile())) {
            throw new ResourceAlreadyExistsException("User with mobile already exists: " + requestDTO.getMobile());
        }
        User user = UserMapper.toEntity(requestDTO);
        user.setUserId(UserIdGeneratorUtil.generateUserId());
        userRepository.save(user);
        log.info("User [{}] successfully registered", user.getUserId());
        return UserMapper.toDTO(user);

    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUserId(String userId) {
        log.info("Fetching user by userId: {}", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));
        // Non-admin users can fetch only active users
        if (!userSecurity.isAdmin()) {
            validateUserIsActive(user);
        }
        return UserMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.info("Fetching all users");
        return mapUsersToDTOs(userRepository.findAll(), "No users found");
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        validateUserIsActive(user); // ensure not DELETED
        return UserMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByMobile(String mobile) {
        log.info("Fetching user by mobile: {}", mobile);
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + mobile));
        validateUserIsActive(user); // ensure not DELETED
        return UserMapper.toDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(String userId, UserRequestDTO requestDTO) {
        log.info("Updating user with userId: {}", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));
        validateUserIsActive(user); // ensure not DELETED
        if (requestDTO.getMobile() != null && !user.getMobile().equals(requestDTO.getMobile())
                && userRepository.existsByMobile(requestDTO.getMobile())) {
            throw new ResourceAlreadyExistsException("Mobile already in use: " + requestDTO.getEmail());
        }
        if (requestDTO.getEmail() != null && !user.getEmail().equals(requestDTO.getEmail())
                && userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use: " + requestDTO.getEmail());
        }
        user.setName(requestDTO.getName());
        user.setMobile(requestDTO.getMobile());
        // Update email only if provided
        if (requestDTO.getEmail() != null && !requestDTO.getEmail().isBlank()) {
            user.setEmail(requestDTO.getEmail());
        }
        // Update about only if provided
        if (requestDTO.getAbout() != null && !requestDTO.getAbout().isBlank()) {
            user.setAbout(requestDTO.getAbout());
        }
        return UserMapper.toDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        log.info("Deleting user with userId: {}", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));
        validateUserIsActive(user); // ensure not DELETED
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
        log.info("User [{}] marked as DELETED in user-service DB", userId);
        // Call auth-service to inactivate authentication record
        UpdateUserStatusRequestDTO statusRequest = UpdateUserStatusRequestDTO.builder()
                .userName(userId)
                .status(UserStatus.DELETED)
                .build();
        try {
            UpdateUserStatusResponseDTO responseDTO = authServiceFeignClient.deleteUser_internal(statusRequest);
            log.info("Auth-service user [{}] also marked as DELETED. Response = [{}]", responseDTO.getUserName(), responseDTO);
        } catch (FeignException.NotFound ex) {
            log.error("Auth-service could not find user [{}]. Rolling back user-service status.", userId, ex);
            throw new ResourceNotFoundException("Auth-service user not found: " + userId);
        }
    }

    @Override
    public UpdateUserStatusResponseDTO updateUserStatus_internal(UpdateUserStatusRequestDTO requestDTO) {
        String userId = requestDTO.getUserName();
        log.info("Updating userStatus with userId: {}", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));
        // Validate: Prevent updating to the same status
        if (user.getStatus() == requestDTO.getStatus()) {
            log.warn("User [{}] already has status [{}], update not required", userId, requestDTO.getStatus());
            throw new ResourceAlreadyExistsException("User already has the requested status: " + requestDTO.getStatus());
        }
        user.setStatus(requestDTO.getStatus());
        User savedUser = userRepository.save(user);
        return UpdateUserStatusResponseDTO.builder()
                .userName(savedUser.getUserId())
                .status(savedUser.getStatus())
                .message("User status updated successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserValidationResponseDTO validateUserForHotelOwner(String userId) {
        log.info("Validating User for Hotel Owner Id: {}", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("User [{}] is not active. Status={}", userId, user.getStatus());
            return UserValidationMapper.toUserValidationResponse(user, "User is not active", false);
        }
        if (user.getUserType() != UserType.HOTEL_MANAGER) {
            log.warn("User [{}] is not a hotel manager. Type={}", userId, user.getUserType());
            return UserValidationMapper.toUserValidationResponse(user, "User is not a hotel owner", false);
        }
        return UserValidationMapper.toUserValidationResponse(user, "Valid hotel owner", true);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByReviewId(String reviewId) {
        log.info("Fetching user by reviewId: {}", reviewId);
        UserIdResponseDTO userIdResponseDTO = reviewServiceClient.getUserIdByReviewId(reviewId);
        String userId = userIdResponseDTO.getUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));
        return UserMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllActiveUsers() {
        log.info("Fetching all active users");
        return mapUsersToDTOs(userRepository.findByStatus(UserStatus.ACTIVE),
                "No active users found");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllActiveHotelOwners() {
        log.info("Fetching all active hotel owners");
        return mapUsersToDTOs(
                userRepository.findByUserTypeAndStatus(UserType.HOTEL_MANAGER, UserStatus.ACTIVE),
                "No active hotel owners found");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllActiveNormalUsers() {
        log.info("Fetching all active normal users");
        return mapUsersToDTOs(
                userRepository.findByUserTypeAndStatus(UserType.NORMAL, UserStatus.ACTIVE),
                "No active normal users found");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllDeletedUsers() {
        log.info("Fetching all deleted users");
        return mapUsersToDTOs(
                userRepository.findByStatus(UserStatus.DELETED),
                "No deleted users found");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllDeletedHotelOwners() {
        log.info("Fetching all deleted hotel owners");
        return mapUsersToDTOs(
                userRepository.findByUserTypeAndStatus(UserType.HOTEL_MANAGER, UserStatus.DELETED),
                "No deleted hotel owners found");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllDeletedNormalUsers() {
        log.info("Fetching all deleted normal users");
        return mapUsersToDTOs(
                userRepository.findByUserTypeAndStatus(UserType.NORMAL, UserStatus.DELETED),
                "No deleted normal users found");
    }

    private List<UserResponseDTO> mapUsersToDTOs(List<User> users, String errorMessage) {
        if (users.isEmpty()) {
            throw new ResourceNotFoundException(errorMessage);
        }
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

}



