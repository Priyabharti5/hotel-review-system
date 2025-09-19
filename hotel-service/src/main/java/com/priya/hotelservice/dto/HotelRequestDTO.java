package com.priya.hotelservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving hotel creation and update requests from clients.
 * Includes validation rules to ensure data integrity before persistence.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating or updating a hotel")
public class HotelRequestDTO {

    /**
     * Name of the hotel.
     */
    @Schema(description = "Name of the hotel", example = "The Grand Palace")
    @NotBlank(message = "Hotel name is required")
    @Size(min = 2, max = 100, message = "Hotel name must be between 2 and 100 characters")
    private String name;

    /**
     * Location or address of the hotel.
     */
    @Schema(description = "Location of the hotel", example = "Mumbai, India")
    @NotBlank(message = "Hotel location is required")
    @Size(min = 2, max = 255, message = "Hotel location must be between 2 and 255 characters")
    private String location;

    /**
     * Optional description about the hotel.
     */
    @Schema(description = "Description about the hotel", example = "Luxury hotel with sea view")
    @Size(max = 500, message = "About section can have at most 500 characters")
    private String about;


    @Size(min = 10, max = 10, message = "UserName Must be of 10 Chars!")
    @Schema(description = "Hotel Owner UserName", example = "1234567890")
    private String ownerUsername; // this is only provided by the admin user while creating new hotel

}
