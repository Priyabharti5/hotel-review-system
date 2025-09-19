package com.priya.userservice.config;

import com.priya.userservice.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom Feign {@link ErrorDecoder} for handling remote service errors.
 *
 * <p>Maps specific HTTP status codes (like {@code 404}) to domain-specific exceptions
 * while delegating unknown cases to the default decoder.</p>
 */
@Hidden
@Slf4j
public class FeignClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign call [{}] failed with status {}", methodKey, response.status());

        if (response.status() == 404) {
            // You can improve this by reading the response body if needed
            return new ResourceNotFoundException("Resource not found when calling remote service: " + methodKey);
        }
        // Fallback to default error decoder for other statuses
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
