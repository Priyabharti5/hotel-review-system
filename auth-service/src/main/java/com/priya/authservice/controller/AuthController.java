package com.priya.authservice.controller;

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
import com.priya.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST Controller for handling authentication and account management operations.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>User registration (normal, hotel manager, admin)</li>
 *     <li>User authentication and JWT token issuance</li>
 *     <li>Password change (self-service)</li>
 *     <li>Account, hotel, and review status management (restricted to Admins)</li>
 *     <li>Logout and token invalidation</li>
 *     <li>Admin user queries (active/inactive users)</li>
 * </ul>
 *
 * <p>Security:</p>
 * <ul>
 *     <li>All endpoints require authentication unless explicitly marked as public (e.g., register, login).</li>
 *     <li>Role-based restrictions applied via {@link PreAuthorize} annotations.</li>
 * </ul>
 *
 * <p>Prefix: All endpoints are under <b>/api/auth</b></p>
 *
 * <p>Errors are returned as standardized {@link ResponseDTO} or service-specific DTOs.</p>
 *
 * @author Priya
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Authentication", description = "Endpoints for authentication and account management")
public class AuthController {

    private final AuthService authService;


    /**
     * Registers a new normal user.
     *
     * @param requestDTO The registration request containing name, email, mobile, about, and password.
     * @return {@link UserRegistrationResponseDTO} containing registered user details excluding password.
     */
    @Operation(summary = "Register a new normal user", description = "Registers a user with USER role. Public endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserRegistrationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or username already exists",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class)))
    })
    @PostMapping("/user/register")
    public ResponseEntity<UserRegistrationResponseDTO> registerUser(@Valid @RequestBody UserRegistrationRequestDTO requestDTO) {
        log.info("API CALL: User Registration with request = {}", requestDTO);
        UserRegistrationResponseDTO responseDTO = authService.registerUser(requestDTO);
        log.info("API SUCCESS: User Registered with userId = {}", responseDTO.getUserName());
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Registers a new hotel manager user.
     *
     * @param requestDTO The registration request.
     * @return {@link UserRegistrationResponseDTO} containing hotel manager user details.
     */
    @Operation(summary = "Register a new Hotel Manager user", description = "Registers a user with HOTEL_MANAGER role. Public endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel Manager registered successfully",
                    content = @Content(schema = @Schema(implementation = UserRegistrationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or username already exists",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class)))
    })
    @PostMapping("/hotel-manager-user/register")
    public ResponseEntity<UserRegistrationResponseDTO> registerHotelManagerUser(
            @Valid @RequestBody UserRegistrationRequestDTO requestDTO) {

        log.info("API CALL: Hotel Manager User Registration with request = {}", requestDTO);
        UserRegistrationResponseDTO responseDTO = authService.registerHotelManagerUser(requestDTO);
        log.info("API SUCCESS: Hotel Manager User Registered with userName = {}", responseDTO.getUserName());

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Registers a new admin user. Can be performed only by Admin role.
     */
    @Operation(summary = "Register a new admin user", description = "Restricted to Admin role. Registers another admin.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin registered successfully",
                    content = @Content(schema = @Schema(implementation = AdminRegisterResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or username already exists",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class)))
    })
    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminRegisterResponseDTO> registerAdminUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("API CALL: Registering Admin User with request = {}", loginRequestDTO);
        AdminRegisterResponseDTO responseDTO = authService.registerAdminUser(loginRequestDTO);
        log.info("API SUCCESS: Admin User Registered with userName = {}", responseDTO.getUserName());

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginRequestDTO The login request containing username and password.
     * @return {@link LoginResponseDTO} containing JWT token and user details.
     */
    @Operation(summary = "Login and get JWT token", description = "Authenticates user and issues JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("API CALL: Login attempt for user = {}", loginRequestDTO.getUserName());
        LoginResponseDTO responseDTO = authService.login(loginRequestDTO);
        log.info("API SUCCESS: Login Completed, Generated TOKEN =  {}", responseDTO.getToken());

        return ResponseEntity.ok(responseDTO);

    }

    /**
     * Logs out the currently authenticated user and invalidates the JWT token.
     *
     * @param header Authorization header containing the Bearer token.
     * @return {@link ResponseDTO} indicating successful logout.
     */
    @Operation(summary = "Logout user and invalidate JWT token", description = "Requires valid authentication.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Token invalid or expired",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class)))
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO> logout(@RequestHeader("Authorization") String header) {
        String token = header.replace("Bearer ", "");
        log.info("API CALL: Logout request (token masked)");
        ResponseDTO responseDTO = authService.logout(token);
        log.info("API SUCCESS: Logout completed");

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Allows an authenticated user to change their own password.
     * <p>
     * Only the owner of the account can change their password. Admin cannot change other users' passwords here.
     *
     * @param authentication Current authentication object from Spring Security context.
     * @return {@link ResponseDTO} indicating success or failure.
     */
    @Operation(summary = "Change password for logged-in user", description = "Only the account owner can change their password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Old password incorrect",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class)))
    })
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDTO> changeUserPassword(
            @Valid @RequestBody ChangePasswordRequestDTO request,
            Authentication authentication) {

        log.info("API CALL: Change password request for user [{}]", request.getUserName());
        ResponseDTO responseDTO = authService.changePassword(request, authentication);
        log.info("API SUCCESS: Password changed for user [{}]", request.getUserName());

        return ResponseEntity.ok(responseDTO);
    }

    @Hidden
    @PutMapping("/_internal/user/delete")
    public ResponseEntity<UpdateUserStatusResponseDTO> deleteUser_internal(@Valid @RequestBody UpdateUserStatusRequestDTO requestDTO) {

        log.info("[Internal]: API CALL: Admin updating status of user [{}] to [{}]", requestDTO.getUserName(), requestDTO.getStatus());
        UpdateUserStatusResponseDTO responseDTO = authService.deleteUser_internal(requestDTO);
        log.info("[Internal]: API SUCCESS: {}", responseDTO);

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Get all admin users (Admin only).
     */
    @Operation(summary = "Get all admin users", description = "Restricted to Admin role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admins retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AdminRegisterResponseDTO.class)))
    })
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminRegisterResponseDTO>> getAllAdmins() {
        log.info("API CALL: Fetching all admin users");
        List<AdminRegisterResponseDTO> admins = authService.getAllAdmins();
        log.info("API SUCCESS: Retrieved [{}] admin users", admins.size());
        return ResponseEntity.ok(admins);
    }

    /**
     * Get all active admin users (Admin only).
     */
    @Operation(summary = "Get all active admin users", description = "Restricted to Admin role.")
    @GetMapping("/admin/users/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminRegisterResponseDTO>> getActiveAdmins() {
        log.info("API CALL: Fetching all ACTIVE admin users");
        List<AdminRegisterResponseDTO> admins = authService.getAdminsByStatus(UserStatus.ACTIVE);
        log.info("API SUCCESS: Retrieved [{}] Active Admin users", admins.size());
        return ResponseEntity.ok(admins);
    }

    /**
     * Get all inactive admin users (Admin only).
     */
    @Operation(summary = "Get all deleted admin users")
    @GetMapping("/admin/users/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminRegisterResponseDTO>> getInactiveAdmins() {
        log.info("API CALL: Fetching all DELETED admin users");
        List<AdminRegisterResponseDTO> admins = authService.getAdminsByStatus(UserStatus.DELETED);
        log.info("API SUCCESS: Retrieved {} deleted admin users", admins.size());
        return ResponseEntity.ok(admins);
    }

    /**
     * Updates the status of a user account. Can be performed only by an Admin.
     *
     * @param requestDTO {@link UpdateUserStatusRequestDTO} containing new status.
     * @return {@link UpdateUserStatusResponseDTO} containing updated user status.
     */
    @Operation(summary = "Update status of a user", description = "Restricted to Admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status updated successfully",
                    content = @Content(schema = @Schema(implementation = UpdateUserStatusResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/admin/user/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpdateUserStatusResponseDTO> updateUserStatus(
            @Valid @RequestBody UpdateUserStatusRequestDTO requestDTO,
            Authentication authentication) {

        log.info("API CALL: Admin updating status of user: [{}] to [{}]", requestDTO.getUserName(), requestDTO.getStatus());
        UpdateUserStatusResponseDTO responseDTO = authService.updateUserStatus(requestDTO, authentication);
        log.info("API SUCCESS: User: [{}] status updated to [{}]", responseDTO.getUserName(), responseDTO.getStatus());

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Update status of a hotel", description = "Restricted to Admin role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel status updated successfully",
                    content = @Content(schema = @Schema(implementation = UpdateHotelStatusResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @PutMapping("/admin/hotel/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpdateHotelStatusResponseDTO> updateHotelStatus(@Valid @RequestBody UpdateHotelStatusRequestDTO requestDTO) {
        log.info("API Call: Admin updating status of hotel: [{}] to [{}]", requestDTO.getHotelId(), requestDTO.getStatus());
        UpdateHotelStatusResponseDTO responseDTO = authService.updateHotelStatus(requestDTO);
        log.info("API Success: Hotel: [{}] status updated to [{}]", responseDTO.getHotelId(), responseDTO.getStatus());

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Update status of a review (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review status updated successfully",
                    content = @Content(schema = @Schema(implementation = UpdateReviewStatusResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PutMapping("/admin/review/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpdateReviewStatusResponseDTO> updateReviewStatus(
            @Valid @RequestBody UpdateReviewStatusRequestDTO requestDTO) {

        log.info("API Call: Admin updating status of review [{}] to [{}]", requestDTO.getReviewId(), requestDTO.getStatus());
        UpdateReviewStatusResponseDTO responseDTO = authService.updateReviewStatus(requestDTO);
        log.info("API Success: Review [{}] status updated to [{}]", responseDTO.getReviewId(), responseDTO.getStatus());

        return ResponseEntity.ok(responseDTO);
    }

}
