package com.priya.userservice.config;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Spring Data JPA Auditing configuration.
 *
 * <p>Provides the current authenticated username for use in
 * {@code @CreatedBy} and {@code @LastModifiedBy} fields.
 * Falls back to {@code SYSTEM} when no user is available
 * (e.g., batch jobs or unauthenticated context).</p>
 */

@Configuration
@Hidden
public class AuditConfig {

    /**
     * Provides the current auditor (username) to JPA.
     *
     * @return an {@link AuditorAware} instance
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of("SYSTEM"); // fallback for system tasks
            }
            return Optional.ofNullable(authentication.getName()); // logged-in username
        };
    }
}
