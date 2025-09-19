package com.priya.userservice.client;

import com.priya.userservice.config.FeignConfig;
import com.priya.userservice.dto.UpdateUserStatusRequestDTO;
import com.priya.userservice.dto.UpdateUserStatusResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with the Auth Service regarding user account updates.
 * <p>
 * This client is hidden from API documentation since it is used for internal service-to-service
 * communication only (via Feign + Eureka).
 * </p>
 */
@FeignClient(
        name = "auth-service",
        configuration = FeignConfig.class,
        path = "/api/auth/"
)
@Validated
@Hidden
public interface AuthServiceFeignClient {

    /**
     * Updates the status of a user in the Auth Service.
     *
     * @param requestDTO contains the username and new status to be applied
     * @return the updated user details including username, role, and status
     */
    @PutMapping("/_internal/user/delete")
    UpdateUserStatusResponseDTO deleteUser_internal(@Valid @RequestBody UpdateUserStatusRequestDTO requestDTO);

}
