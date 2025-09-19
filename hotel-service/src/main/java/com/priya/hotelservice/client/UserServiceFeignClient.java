package com.priya.hotelservice.client;

import com.priya.hotelservice.config.FeignConfig;
import com.priya.hotelservice.dto.UserValidationResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communication with the User Service.
 * Used for internal validations such as checking hotel ownership.
 */
@FeignClient(
        name = "user-service",
        configuration = FeignConfig.class,
        path = "/api/users"
)
@Hidden
public interface UserServiceFeignClient {

    /**
     * Validate whether the given user is a registered hotel owner.
     *
     * @param userId the user ID to validate
     * @return {@link UserValidationResponseDTO} containing validation details
     */
    @GetMapping("_internal/validate/{userId}")
    UserValidationResponseDTO validateHotelOwner(@PathVariable("userId") String userId);

}
