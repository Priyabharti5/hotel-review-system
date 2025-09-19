package com.priya.reviewservice.client;

import com.priya.reviewservice.config.FeignConfig;
import com.priya.reviewservice.dto.HotelResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for communication with the Hotel Service.
 * <p>
 * Provides internal methods to fetch hotel details, update ratings,
 * and retrieve hotel IDs owned by a specific user.
 * </p>
 *
 * <p><b>Note:</b> This client is used only for inter-service communication
 * and is hidden from Swagger documentation.</p>
 *
 * @see FeignConfig
 */
@FeignClient(
        name = "hotel-service",
        configuration = FeignConfig.class,
        path = "/api/hotels"
)
@Hidden
public interface HotelServiceClient {

    /**
     * Fetch hotel details by hotelId from hotel-service.
     *
     * @param hotelId unique identifier of the hotel
     * @return hotel details as {@link HotelResponseDTO}
     * @throws feign.FeignException if the hotel-service call fails
     */
    @GetMapping("/_internal/review/{hotelId}")
    HotelResponseDTO getHotelByHotelId(@PathVariable("hotelId") String hotelId);

    /**
     * Update the average rating of a hotel in hotel-service.
     *
     * @param hotelId       unique identifier of the hotel
     * @param averageRating new calculated average rating
     * @throws feign.FeignException if the hotel-service call fails
     */
    @PutMapping("/_internal/review/rating/{hotelId}")
    void updateHotelRating(
            @PathVariable("hotelId") String hotelId,
            @RequestParam("averageRating") Double averageRating);


    /**
     * Retrieve all hotel IDs owned by a given user.
     *
     * @param ownerUserName username of the hotel owner
     * @return list of hotel IDs owned by the specified user
     * @throws feign.FeignException if the hotel-service call fails
     */
    @GetMapping("/_internal/review/owner/{ownerUserName}")
    List<String> getHotelIdsByOwnerUserName(@PathVariable("ownerUserName") String ownerUserName);


}
