package com.priya.authservice.service.impl;

import com.priya.authservice.client.HotelServiceClient;
import com.priya.authservice.client.ReviewServiceClient;
import com.priya.authservice.client.UserServiceClient;
import com.priya.authservice.dto.admin_registration.AdminRegisterResponseDTO;
import com.priya.authservice.dto.common.ResponseDTO;
import com.priya.authservice.dto.hotel_status.UpdateHotelStatusRequestDTO;
import com.priya.authservice.dto.hotel_status.UpdateHotelStatusResponseDTO;
import com.priya.authservice.dto.login.LoginRequestDTO;
import com.priya.authservice.dto.login.LoginResponseDTO;
import com.priya.authservice.dto.password.ChangePasswordRequestDTO;
import com.priya.authservice.dto.review_status.UpdateReviewStatusRequestDTO;
import com.priya.authservice.dto.review_status.UpdateReviewStatusResponseDTO;
import com.priya.authservice.dto.user_status.UpdateUserStatusRequestDTO;
import com.priya.authservice.dto.user_status.UpdateUserStatusResponseDTO;
import com.priya.authservice.dto.user_registration.UserResponseDTO;
import com.priya.authservice.entity.AuthUser;
import com.priya.authservice.entity.Role;
import com.priya.authservice.enums.RoleName;
import com.priya.authservice.enums.UserStatus;
import com.priya.authservice.enums.UserType;
import com.priya.authservice.exception.InvalidCredentialsException;
import com.priya.authservice.exception.InvalidUserStateException;
import com.priya.authservice.exception.ResourceAlreadyExistsException;
import com.priya.authservice.exception.ResourceNotFoundException;
import com.priya.authservice.mapper.UserRegistrationMapper;
import com.priya.authservice.repository.AuthUserRepository;
import com.priya.authservice.repository.RoleRepository;
import com.priya.authservice.security.AuthCustomUserDetails;
import com.priya.authservice.security.JwtTokenStore;
import com.priya.authservice.security.JwtUtil;
import com.priya.authservice.service.AuthService;
import com.priya.authservice.dto.user_registration.UserRegistrationRequestDTO;
import com.priya.authservice.dto.user_registration.UserRegistrationResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Service implementation for {@link AuthService}, handling authentication,
 * authorization, and user account lifecycle management.
 *
 * <p>Handles authentication, authorization, and lifecycle management for users,
 * hotel managers, admins, hotels, and reviews. Integrates with downstream
 * microservices via Feign clients and enforces role-based access control.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>JWT-based authentication with active token tracking</li>
 *   <li>Role-based user registration (user, hotel manager, admin)</li>
 *   <li>Status management with guard clauses to avoid redundant updates</li>
 *   <li>Structured logging at DEBUG/INFO/WARN levels</li>
 *   <li>Validation with {@code @Valid} and explicit null/ownership checks</li>
 *   <li>Transactional guarantees on mutating methods</li>
 * </ul>
 *
 * @author Priya
 */

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userServiceClient;
    private final HotelServiceClient hotelServiceClient;
    private final ReviewServiceClient reviewServiceClient;

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtTokenStore jwtTokenStore;

    // ======================== Helper Methods ======================== //

    private String getUserId(Authentication authentication) {
        return authentication.getName(); // username = ownerId in JWT
    }

    private String getUserRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("No role assigned to user"));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void validateOwnershipOrAdmin(String resourceOwnerId, Authentication authentication) {
        String userId = getUserId(authentication);
        if (!isAdmin(authentication) && !resourceOwnerId.equals(userId)) {
            throw new AccessDeniedException("Access denied: not owner or admin");
        }
    }

    // ======================== Service Methods ======================== //

    /**
     * Registers a normal end-user with default role {@code ROLE_USER}.
     */
    @Override
    @Transactional
    public UserRegistrationResponseDTO registerUser(UserRegistrationRequestDTO requestDTO) {
        log.debug("Registering new user with email [{}]", requestDTO.getEmail());

        UserResponseDTO userResponse = userServiceClient.registerUser(UserRegistrationMapper.toUserServiceRequest(requestDTO, UserStatus.ACTIVE, UserType.NORMAL));
        String userId = userResponse.getUserId();

        Role defaultRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> {
                    log.error("Default role ROLE_USER not found");
                    return new ResourceNotFoundException("Role: ROLE_USER not found");
                });

        AuthUser authUser = AuthUser.builder()
                .username(userId)
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .status(UserStatus.ACTIVE) // default status
                .role(defaultRole)
                .build();

        authUserRepository.save(authUser);

        log.info("User [{}] registered successfully with role ROLE_USER", authUser.getUsername());

        return UserRegistrationMapper.toRegistrationResponse(userResponse, authUser);
    }

    /**
     * Registers a hotel manager user with role {@code ROLE_HOTEL_MANAGER}.
     */
    @Override
    @Transactional
    public UserRegistrationResponseDTO registerHotelManagerUser(UserRegistrationRequestDTO requestDTO) {

        log.debug("Registering hotel manager with email [{}]", requestDTO.getEmail());

        UserResponseDTO userResponse = userServiceClient.registerUser(
                UserRegistrationMapper.toUserServiceRequest(requestDTO, UserStatus.ACTIVE, UserType.HOTEL_MANAGER)
        );

        String userId = userResponse.getUserId();

        Role managerRole = roleRepository.findByName(RoleName.ROLE_HOTEL_MANAGER)
                .orElseThrow(() -> {
                    log.error("Role ROLE_HOTEL_MANAGER not found");
                    return new ResourceNotFoundException("Role: ROLE_HOTEL_MANAGER not found");
                });

        AuthUser authUser = AuthUser.builder()
                .username(userId)
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .status(UserStatus.ACTIVE)
                .role(managerRole)
                .build();

        authUserRepository.save(authUser);

        log.info("Hotel manager [{}] registered successfully", authUser.getUsername());

        return UserRegistrationMapper.toRegistrationResponse(userResponse, authUser);
    }

    /**
     * Registers a new admin account.
     *
     * @throws ResourceAlreadyExistsException if the username already exists
     */
    @Override
    public AdminRegisterResponseDTO registerAdminUser(LoginRequestDTO loginRequestDTO) {
        log.debug("Registering admin user [{}]", loginRequestDTO.getUserName());

        if (authUserRepository.findByUsername(loginRequestDTO.getUserName()).isPresent()) {
            log.warn("Registration failed - Username [{}] already exists", loginRequestDTO.getUserName());
            throw new ResourceAlreadyExistsException("User Already Exists!");
        }
        RoleName roleName = RoleName.ROLE_ADMIN;

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(null, roleName)));

        AuthUser newUser = new AuthUser();
        newUser.setUsername(loginRequestDTO.getUserName());
        newUser.setPassword(passwordEncoder.encode(loginRequestDTO.getPassword()));
        newUser.setRole(role);
        newUser.setStatus(UserStatus.ACTIVE);

        AuthUser savedUser = authUserRepository.save(newUser);

        log.info("Admin User registered successfully with userName: {}", savedUser.getUsername());

        return AdminRegisterResponseDTO.builder()
                .userName(savedUser.getUsername())
                .status(savedUser.getStatus())
                .roleName(savedUser.getRole().getName())
                .build();
    }

    /**
     * Authenticates a user and issues a JWT token.
     *
     * @throws ResourceNotFoundException if authentication fails
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        log.debug("Login attempt for user [{}]", loginRequestDTO.getUserName());

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUserName(), loginRequestDTO.getPassword()));

            AuthCustomUserDetails principal = (AuthCustomUserDetails) auth.getPrincipal();

            // check if user is active
            if (principal.getStatus() == null || !principal.getStatus().equals(UserStatus.ACTIVE)) {
                log.warn("Login blocked for inactive user [{}] with status [{}]",
                        principal.getUsername(), principal.getStatus());
                throw new InvalidUserStateException("User account is not active. Please contact support.");
            }

            String role = principal.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");

            String token = jwtUtil.generateToken(principal.getUsername(), role);
            // store active token for logout validation
            jwtTokenStore.addToken(token);

            log.info("User [{}] logged in successfully with role [{}]", principal.getUsername(), role);

            return new LoginResponseDTO(token, "Bearer", principal.getUsername());

        } catch (AuthenticationException e) {
            log.warn("Invalid login attempt for user: [{}]. Reason: {}", loginRequestDTO.getUserName(), e.getMessage());
            throw new InvalidCredentialsException(e.getMessage());
        }
    }

    /**
     * Logs out a user by invalidating their JWT token.
     */
    @Override
    public ResponseDTO logout(String token) {

        if (!jwtTokenStore.isTokenActive(token)) {
            log.warn("Logout failed - token already inactive or expired");
            throw new ResourceNotFoundException("Token is Already Invalid or Expired!");
        }

        jwtTokenStore.removeToken(token); // Remove token from in-memory store
        String username = jwtUtil.extractUsername(token);

        log.info("User [{}] logged out successfully, token invalidated", username);

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.toString())
                .statusMessage("User logged out successfully with userName: " + username)
                .build();
    }

    /**
     * Marks a user as deleted (internal use).
     */
    @Override
    @Transactional
    public UpdateUserStatusResponseDTO deleteUser_internal(UpdateUserStatusRequestDTO requestDTO) {
        String username = requestDTO.getUserName();
        log.debug("Deleting user [{}]", username);

        AuthUser user = authUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Delete failed - user [{}] not found", username);
                    return new ResourceNotFoundException("User not found: " + username);
                });

        if (user.getStatus() == UserStatus.DELETED) {
            log.info("User [{}] is already deleted", username);
            return UpdateUserStatusResponseDTO.builder()
                    .userName(username)
                    .status(user.getStatus())
                    .message("User [" + username + "] is already deleted")
                    .build();
        }

        user.setStatus(UserStatus.DELETED);
        authUserRepository.save(user);

        log.info("User [{}] marked as deleted", username);

        return UpdateUserStatusResponseDTO.builder()
                .userName(username)
                .status(UserStatus.DELETED)
                .message("User [" + username + "] has been marked as deleted")
                .build();
    }

    /**
     * Changes the password for the specified user.
     *
     * @throws ResourceNotFoundException if user does not exist or old password is invalid
     */
    @Override
    @Transactional
    public ResponseDTO changePassword(ChangePasswordRequestDTO request, Authentication authentication) {

        log.debug("Password change request for user [{}]", request.getUserName());

        // check for given current and new password values it should not be same:
        if (Objects.equals(request.getOldPassword(), request.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password");
        }

        Objects.requireNonNull(authentication, "Authentication must not be null");

        // Enforce ownership check: user can only change their own password
        if (!authentication.getName().equals(request.getUserName())) {
            return ResponseDTO.builder()
                    .statusCode(HttpStatus.FORBIDDEN.toString())
                    .statusMessage("You can only change your own password")
                    .build();
        }

        AuthUser user = authUserRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> {
                    log.warn("Password change failed - user [{}] not found", request.getUserName());
                    return new ResourceNotFoundException("User not found: " + request.getUserName());
                });

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Password change failed - invalid old password for user [{}]", request.getUserName());
            throw new ResourceNotFoundException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        AuthUser savedAuthUser = authUserRepository.save(user);

        log.info("Password updated successfully for user: {}", savedAuthUser.getUsername());

        return ResponseDTO.builder()
                .statusCode(HttpStatus.OK.toString())
                .statusMessage("Password updated successfully for user: " + user.getUsername())
                .build();
    }

    /**
     * Retrieves all admin users.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AdminRegisterResponseDTO> getAllAdmins() {
        log.debug("Fetching all admin users");
        Role role = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        return authUserRepository.findByRole(role).stream()
                .map(user -> AdminRegisterResponseDTO.builder()
                        .userName(user.getUsername())
                        .status(user.getStatus())
                        .roleName(user.getRole().getName())
                        .build())
                .toList();
    }

    /**
     * Retrieves admin users filtered by status.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AdminRegisterResponseDTO> getAdminsByStatus(UserStatus status) {
        log.debug("Fetching admin users with status {}", status);
        Role role = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        return authUserRepository.findByRoleAndStatus(role, status).stream()
                .map(user -> AdminRegisterResponseDTO.builder()
                        .userName(user.getUsername())
                        .status(user.getStatus())
                        .roleName(user.getRole().getName())
                        .build())
                .toList();
    }

    /**
     * Updates a user's status (admin only).
     * Returns early if the status is unchanged.
     */
    @Override
    @Transactional
    public UpdateUserStatusResponseDTO updateUserStatus(UpdateUserStatusRequestDTO requestDTO, Authentication authentication) {
        String username = requestDTO.getUserName();
        log.debug("Admin requested status update for user [{}] to [{}]", username, requestDTO.getStatus());

        AuthUser user = authUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User [{}] not found for status update", username);
                    return new ResourceNotFoundException("User not found: " + username);
                });

        UserStatus currentStatus = user.getStatus();

        // Guard clause: prevent redundant update
        if (currentStatus == requestDTO.getStatus()) {
            log.info("No update performed. User [{}] already has status [{}]", username, currentStatus);
            return UpdateUserStatusResponseDTO.builder()
                    .userName(user.getUsername())
                    .status(currentStatus)
                    .message("User already has status: " + currentStatus)
                    .build();
        }

        log.info("Updating status of user [{}] from [{}] to [{}]", username, currentStatus, requestDTO.getStatus());

        user.setStatus(requestDTO.getStatus());
        AuthUser updatedUser = authUserRepository.save(user);

        String role = updatedUser.getRole().getName().toString();

        if (!role.equals("ROLE_ADMIN")) {
            UpdateUserStatusRequestDTO userStatusRequestDTO = UpdateUserStatusRequestDTO.builder()
                    .userName(updatedUser.getUsername())
                    .status(updatedUser.getStatus())
                    .build();
            UpdateUserStatusResponseDTO responseDTO = userServiceClient.updateUserStatus(userStatusRequestDTO);

            log.info("user-service user status updated via feign: response: {}", responseDTO);
        }

        return UpdateUserStatusResponseDTO.builder()
                .userName(updatedUser.getUsername())
                .status(updatedUser.getStatus())
                .message("Auth User Status updated successfully")
                .build();
    }

    /**
     * Updates the status of a hotel (Admin only).
     */
    @Override
    public UpdateHotelStatusResponseDTO updateHotelStatus(@Valid UpdateHotelStatusRequestDTO requestDTO) {

        log.debug("Admin requested status update for Hotel [{}] to [{}]", requestDTO.getHotelId(), requestDTO.getStatus());

        // Call FeignClient
        UpdateHotelStatusResponseDTO responseDTO = hotelServiceClient.updateHotelStatusClient(requestDTO);

        log.info("hotel-service hotel status updated via feign: response: {}", responseDTO);

        return UpdateHotelStatusResponseDTO.builder()
                .hotelId(responseDTO.getHotelId())
                .status(responseDTO.getStatus())
                .message(responseDTO.getMessage())
                .build();
    }

    /**
     * Updates the status of a review (Admin only).
     */
    @Override
    public UpdateReviewStatusResponseDTO updateReviewStatus(UpdateReviewStatusRequestDTO requestDTO) {
        log.info("Admin updating review [{}] to status [{}]", requestDTO.getReviewId(), requestDTO.getStatus());
        UpdateReviewStatusResponseDTO response = reviewServiceClient.updateReviewStatus_internal(requestDTO);
        log.info("review-service review status updated via feign: response: {}", response);
        return response;
    }

}
