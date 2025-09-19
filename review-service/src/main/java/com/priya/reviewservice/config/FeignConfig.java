package com.priya.reviewservice.config;

import feign.codec.ErrorDecoder;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Feign client configuration for propagating authenticated user context
 * and customizing error decoding.
 */
@Configuration
@Slf4j
@Hidden
public class FeignConfig {

    /**
     * Interceptor to propagate user identity in Feign requests.
     *
     * @return interceptor that adds headers
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                String username = auth.getName();
                String role = auth.getAuthorities().stream()
                        .findFirst()
                        .map(Object::toString)
                        .orElse("ROLE_USER");

                template.header("X-User-Name", username);
                template.header("X-User-Role", role);

                log.debug("Added Feign headers for user [{}] with role [{}]", username, role);
            }
        };
    }


    /**
     * Provides a custom error decoder for Feign.
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignClientErrorDecoder();
    }
}
