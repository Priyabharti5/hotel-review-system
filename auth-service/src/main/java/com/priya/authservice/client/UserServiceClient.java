package com.priya.authservice.client;

import com.priya.authservice.config.FeignConfig;
import com.priya.authservice.dto.user_status.UpdateUserStatusRequestDTO;
import com.priya.authservice.dto.user_status.UpdateUserStatusResponseDTO;
import com.priya.authservice.dto.user_registration.UserRequestDTO;
import com.priya.authservice.dto.user_registration.UserResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with the {@code user-service}.
 * <p>
 * Internal-only client used by Auth Service for:
 * <ul>
 *   <li>Registering new users in user-service.</li>
 *   <li>Updating user status (activation/deactivation).</li>
 * </ul>
 */
@FeignClient(
        name = "user-service",
        configuration = FeignConfig.class,
        path = "/api/users")
@Validated
@Hidden
public interface UserServiceClient {

    /**
     * Registers a new user in the User Service.
     *
     * @param userRequestDTO request payload with user details
     * @return {@link UserResponseDTO} containing persisted user info
     */
    @PostMapping("/_internal/register")
    UserResponseDTO registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO);


    /**
     * Updates the status of an existing user in User Service.
     *
     * @param requestDTO request payload containing username and new status
     * @return response with updated user status
     */
    @PutMapping("/_internal/user/status")
    UpdateUserStatusResponseDTO updateUserStatus(@Valid @RequestBody UpdateUserStatusRequestDTO requestDTO);

}
