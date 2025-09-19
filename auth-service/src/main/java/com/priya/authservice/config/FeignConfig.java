package com.priya.authservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Feign client configuration for propagating security context
 * and customizing error decoding.
 *
 * <p><b>Hidden from Swagger:</b> This config is not exposed as an API endpoint.</p>
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
