package com.priya.reviewservice.client;

import com.priya.reviewservice.config.FeignConfig;
import com.priya.reviewservice.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * Feign client for interacting with the User Service.
 * <p>
 * Provides internal methods to fetch user details.
 * </p>
 *
 * <p><b>Note:</b> This client is used only for inter-service communication
 * and is hidden from Swagger documentation.</p>
 *
 * @see FeignConfig
 */
@FeignClient(
        name = "user-service",
        configuration = FeignConfig.class,
        path = "/api/users")
@Hidden
public interface UserServiceClient {

    /**
     * Fetch user details by userId from user-service.
     *
     * @param userId unique identifier of the user
     * @return user details as {@link UserResponseDTO}
     * @throws feign.FeignException if the user-service call fails
     */
    @GetMapping("/_internal/{userId}")
    UserResponseDTO getUserById(@PathVariable("userId") String userId);

}
