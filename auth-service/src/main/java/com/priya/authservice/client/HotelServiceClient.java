package com.priya.authservice.client;

import com.priya.authservice.config.FeignConfig;
import com.priya.authservice.dto.hotel_status.UpdateHotelStatusRequestDTO;
import com.priya.authservice.dto.hotel_status.UpdateHotelStatusResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with the {@code hotel-service}.
 * <p>
 * This is an internal client used by Auth Service for updating hotel status
 * (e.g., when the owning user account is disabled). Not exposed to external clients.
 */
@FeignClient(
        name = "hotel-service",
        configuration = FeignConfig.class,
        path = "/api/hotels")
@Validated
@Hidden     // Prevents this interface from showing up in Swagger
public interface HotelServiceClient {

    /**
     * Calls Hotel Service to update the status of a hotel.
     *
     * @param requestDTO request payload containing hotelId and new status
     * @return response with updated status confirmation
     */
    @PutMapping("/_internal/hotel/status")
    UpdateHotelStatusResponseDTO updateHotelStatusClient(@Valid @RequestBody UpdateHotelStatusRequestDTO requestDTO);
}
