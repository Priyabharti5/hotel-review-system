package com.priya.authservice.service;

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
import com.priya.authservice.dto.user_registration.UserRegistrationRequestDTO;
import com.priya.authservice.dto.user_registration.UserRegistrationResponseDTO;
import com.priya.authservice.enums.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;

import java.util.List;


/**
 * Authentication and User Account Management Service.
 *
 * <p>This service defines all user lifecycle operations such as registration,
 * authentication, password management, account status updates, and logout.
 *
 * <h2>Security Rules</h2>
 * <ul>
 *   <li>Normal user and hotel manager registration is public</li>
 *   <li>Admin registration is restricted to existing admin accounts</li>
 *   <li>User, hotel, and review status changes are restricted to ADMIN role</li>
 *   <li>Password changes require authentication of the requesting account owner</li>
 * </ul>
 *
 * <h2>Validation</h2>
 * <ul>
 *   <li>All request DTOs must be annotated with {@link Valid}</li>
 *   <li>Enums and mandatory fields must be annotated with {@link NotNull}</li>
 * </ul>
 *
 * <h2>Logging & Observability</h2>
 * <ul>
 *   <li>All methods must log entry/exit points with correlation IDs</li>
 *   <li>Errors should be logged at WARN/ERROR level with cause details</li>
 *   <li>Successful operations should be logged at INFO level</li>
 * </ul>
 *
 * @author Priya
 */

@Tag(name = "Authentication Service", description = "Service for user authentication and account management")
public interface AuthService {

    /**
     * Register a new normal user with role {@code USER}.
     *
     * @param requestDTO Registration request containing user details (validated).
     * @return {@link UserRegistrationResponseDTO} with registered user details.
     */
    @Operation(summary = "Register normal user", description = "Creates a new user account with role USER")
    UserRegistrationResponseDTO registerUser(UserRegistrationRequestDTO requestDTO);

    /**
     * Register a new hotel manager with role {@code HOTEL_MANAGER}.
     *
     * @param requestDTO Registration request containing manager details (validated).
     * @return {@link UserRegistrationResponseDTO} with registered hotel manager details.
     */
    @Operation(summary = "Register hotel manager", description = "Creates a new hotel manager account with role HOTEL_MANAGER")
    UserRegistrationResponseDTO registerHotelManagerUser(UserRegistrationRequestDTO requestDTO);

    /**
     * Register a new administrator with role {@code ADMIN}.
     *
     * @param loginRequestDTO Admin registration request with credentials.
     * @return {@link AdminRegisterResponseDTO} containing registered admin details.
     */
    @Operation(summary = "Register admin", description = "Creates a new admin account. Only accessible to existing ADMIN users")
    AdminRegisterResponseDTO registerAdminUser(LoginRequestDTO loginRequestDTO);

    /**
     * Authenticate a user and generate a JWT token.
     *
     * @param loginRequestDTO Login request with username and password.
     * @return {@link LoginResponseDTO} containing authentication token and metadata.
     */
    @Operation(summary = "User login", description = "Authenticates user credentials and generates JWT token")
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    /**
     * Invalidate a JWT token and log the user out.
     *
     * @param token JWT token to invalidate.
     * @return {@link ResponseDTO} confirming logout success.
     */
    @Operation(summary = "Logout", description = "Invalidates the provided JWT token to log the user out")
    ResponseDTO logout(String token);

    /**
     * Internal soft deletion of a user account.
     *
     * @param requestDTO DTO containing username and status update to DELETED.
     * @return {@link UpdateUserStatusResponseDTO} confirming deletion.
     */
    @Operation(summary = "Delete user (internal)", description = "Soft delete a user by marking status as DELETED. Admin use only.")
    UpdateUserStatusResponseDTO deleteUser_internal(UpdateUserStatusRequestDTO requestDTO);

    /**
     * Change password for the authenticated user.
     *
     * @param request        DTO containing old and new password.
     * @param authentication Authentication context of the logged-in user.
     * @return {@link ResponseDTO} indicating operation result.
     */
    @Operation(summary = "Change password", description = "Allows logged-in user to update their password")
    ResponseDTO changePassword(ChangePasswordRequestDTO request, Authentication authentication);

    /**
     * Get all registered admins.
     *
     * @return List of {@link AdminRegisterResponseDTO} with admin details.
     */
    @Operation(summary = "Get all admins", description = "Fetch all registered administrators")
    List<AdminRegisterResponseDTO> getAllAdmins();

    /**
     * Get admins filtered by status.
     *
     * @param status {@link UserStatus} filter (ACTIVE, SUSPENDED, etc.).
     * @return List of {@link AdminRegisterResponseDTO} matching status.
     */
    @Operation(summary = "Get admins by status", description = "Fetch administrators filtered by account status")
    List<AdminRegisterResponseDTO> getAdminsByStatus(UserStatus status);

    /**
     * Update the status of a user account.
     *
     * @param requestDTO     Request DTO containing username and new status.
     * @param authentication Authentication context of the admin.
     * @return {@link UpdateUserStatusResponseDTO} containing updated status details.
     */
    @Operation(summary = "Update user status", description = "Allows admin to update user account status (ACTIVE, SUSPENDED, DELETED, EXPIRED)")
    UpdateUserStatusResponseDTO updateUserStatus(UpdateUserStatusRequestDTO requestDTO, Authentication authentication);

    /**
     * Update the status of a hotel.
     *
     * @param requestDTO DTO containing hotel identifier and new status.
     * @return {@link UpdateHotelStatusResponseDTO} with updated hotel status.
     */
    @Operation(summary = "Update hotel status", description = "Allows admin to update the operational status of a hotel")
    UpdateHotelStatusResponseDTO updateHotelStatus(UpdateHotelStatusRequestDTO requestDTO);

    /**
     * Update the status of a review.
     *
     * @param requestDTO DTO containing review identifier and new status.
     * @return {@link UpdateReviewStatusResponseDTO} with updated review status.
     */
    @Operation(summary = "Update review status", description = "Allows admin to update the approval status of reviews")
    UpdateReviewStatusResponseDTO updateReviewStatus(@Valid UpdateReviewStatusRequestDTO requestDTO);

}
