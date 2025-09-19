package com.priya.userservice.controller;

import com.priya.userservice.dto.*;
import com.priya.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user-related operations in the system.
 * <p>
 * Supported operations:
 * <ul>
 *   <li>Create a new user</li>
 *   <li>Get user by userId, email, or mobile</li>
 *   <li>Get all users</li>
 *   <li>Update user details</li>
 *   <li>Delete a user</li>
 *   <li>Manage active/deleted users (hotel + normal)</li>
 *   <li>Internal APIs for Auth-Service & Hotel-Service</li>
 * </ul>
 * Base path: <b>/api/users</b>
 * <p>
 * Delegates all business logic to {@link UserService}.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "User APIs", description = "Operations related to Users")
public class UserController {

    private final UserService userService;

    // ===================== INTERNAL APIs =====================

    @PostMapping("/_internal/register")
    @Hidden
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("API Call [INTERNAL] : Register a new user={}", requestDTO);
        UserResponseDTO registeredUser = userService.registerUser_internal(requestDTO);
        log.info("API Success [INTERNAL] : User Registered={}", registeredUser.getUserId());
        return ResponseEntity.status(201).body(registeredUser);
    }

    @GetMapping("/_internal/{userId}")
    @Hidden
    public ResponseEntity<UserResponseDTO> getUserById_internal(@PathVariable("userId") String userId) {
        log.info("[Internal]: API Call: Get user by userId={}", userId);
        UserResponseDTO response = userService.getUserByUserId(userId);
        log.info("[Internal]: API Success: Fetched userId={}", response.getUserId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/_internal/user/status")
    @Hidden
    public ResponseEntity<UpdateUserStatusResponseDTO> updateUserStatus_internal(@Valid @RequestBody UpdateUserStatusRequestDTO requestDTO) {
        log.info("[INTERNAL] API Call: updating user status: {}", requestDTO);
        UpdateUserStatusResponseDTO responseDTO = userService.updateUserStatus_internal(requestDTO);
        log.info("[INTERNAL] API Success: user status updated: {}", responseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("_internal/validate/{userId}")
    @Hidden
    public ResponseEntity<UserValidationResponseDTO> validateUserForHotelOwner(@PathVariable("userId") String userId) {
        log.info("[INTERNAL] API Call: Validate user for hotel owner, userId={}", userId);
        UserValidationResponseDTO response = userService.validateUserForHotelOwner(userId);
        log.info("[INTERNAL] API Success: User Validated for hotel owner, userId= [{}]", userId);
        return ResponseEntity.ok(response);
    }

    // ===================== PUBLIC APIs =====================

    @Operation(summary = "Get user by User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{userId}")
    @PreAuthorize("@userSecurity.isSelfUser(#userId, authentication.name) or hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("userId") String userId) {
        log.info("API Call: Get user by userId={}", userId);
        UserResponseDTO response = userService.getUserByUserId(userId);
        log.info("API Success: Fetched userId={}", response.getUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users fetched successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))))
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("API Call: Fetch all users");
        List<UserResponseDTO> users = userService.getAllUsers();
        log.info("API Success: Fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by Email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSelfUserByEmail(#email, authentication.name)")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@Email(message = "Invalid email format") @PathVariable("email") String email) {
        log.info("API Call: Get user by email: [{}]", email);
        UserResponseDTO user = userService.getUserByEmail(email);
        log.info("API Success: Fetched userId={} for email={}", user.getUserId(), email);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get user by Mobile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/mobile/{mobile}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSelfUserByMobile(#mobile, authentication.name)")
    public ResponseEntity<UserResponseDTO> getUserByMobile(
            @Pattern(regexp = "\\d{10}", message = "Mobile number must be exactly 10 digits")
            @PathVariable("mobile") String mobile) {
        log.info("API Call: Get user by mobile={}", mobile);
        UserResponseDTO user = userService.getUserByMobile(mobile);
        log.info("API Success: Fetched userId={} for mobile={}", user.getUserId(), mobile);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{userId}")
    @PreAuthorize("@userSecurity.isSelfUser(#userId, authentication.name) or hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("API Call: Update userId={}, request={}", userId, requestDTO);
        UserResponseDTO updatedUser = userService.updateUser(userId, requestDTO);
        log.info("API Success: Updated userId = {}", updatedUser.getUserId());
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user by User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{userId}")
    @PreAuthorize("@userSecurity.isSelfUser(#userId, authentication.name) or hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable("userId") String userId) {
        log.info("API Call: Delete userId={}", userId);
        userService.deleteUser(userId);
        log.info("API Success: Deleted userId={}", userId);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .statusCode("200")
                        .statusMessage("User deleted successfully")
                        .build()
        );
    }

    @Operation(summary = "Get user by Review ID", description = "Fetch user details linked with a given review")
    @GetMapping("/review/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserByReviewId(@PathVariable("reviewId") String reviewId) {
        log.info("API Call: Get user by reviewId={}", reviewId);
        UserResponseDTO response = userService.getUserByReviewId(reviewId);
        log.info("API Success: Fetched user={}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all active users (hotel + normal users)", description = "Accessible only by ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of active users retrieved successfully")
    })
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllActiveUsers() {
        log.info("API Call: Get all active users (hotel + normal users)");
        List<UserResponseDTO> users = userService.getAllActiveUsers();
        log.info("API Success: active users: [{}]", users.size());
        return ResponseEntity.ok(users);
    }


    @Operation(summary = "Get all active hotel users", description = "Accessible only by ADMIN")
    @GetMapping("/hotel-owner/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllActiveHotelOwners() {
        log.info("API Call: Get all active hotel users");
        List<UserResponseDTO> hotelOwners = userService.getAllActiveHotelOwners();
        log.info("API Success: all active hotel owners: [{}]", hotelOwners.size());
        return ResponseEntity.ok(hotelOwners);
    }

    @Operation(summary = "Get all active normal users", description = "Accessible only by ADMIN")
    @GetMapping("/normal-user/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllActiveNormalUsers() {
        log.info("API Call: Get all active normal users");
        List<UserResponseDTO> allActiveNormalUsers = userService.getAllActiveNormalUsers();
        log.info("API Success: all active normal users: [{}]", allActiveNormalUsers.size());
        return ResponseEntity.ok(allActiveNormalUsers);
    }

    @Operation(summary = "Get all deleted users (hotel + normal users)", description = "Accessible only by ADMIN")
    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllDeletedUsers() {
        log.info("API Call: Get all deleted users (hotel + normal users)");
        List<UserResponseDTO> allDeletedUsers = userService.getAllDeletedUsers();
        log.info("API Success: all deleted users (hotel + normal users): [{}]", allDeletedUsers.size());
        return ResponseEntity.ok(allDeletedUsers);
    }

    @Operation(summary = "Get all deleted hotel users", description = "Accessible only by ADMIN")
    @GetMapping("/hotel-owner/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllDeletedHotelOwners() {
        log.info("API Call: Get all deleted hotel users");
        List<UserResponseDTO> allDeletedHotelOwners = userService.getAllDeletedHotelOwners();
        log.info("API Call: all deleted hotel users: [{}]", allDeletedHotelOwners.size());
        return ResponseEntity.ok(allDeletedHotelOwners);
    }

    @Operation(summary = "Get all deleted normal users", description = "Accessible only by ADMIN")
    @GetMapping("/normal-user/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllDeletedNormalUsers() {
        log.info("API Call: Get all deleted normal users");
        List<UserResponseDTO> allDeletedNormalUsers = userService.getAllDeletedNormalUsers();
        log.info("API Call: all deleted normal users: [{}]", allDeletedNormalUsers);
        return ResponseEntity.ok(allDeletedNormalUsers);
    }

}
