package com.priya.authservice.config;

import com.priya.authservice.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom Feign error decoder to handle HTTP error responses gracefully.
 *
 * <p>Maps specific status codes (e.g., {@code 404}) to domain exceptions.</p>
 */
@Slf4j
@Hidden
public class FeignClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign call [{}] failed with status {}", methodKey, response.status());

        if (response.status() == 404) {
            return new ResourceNotFoundException("Resource not found when calling remote service: " + methodKey);
        }
        // Fallback to default error decoder for other statuses
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
