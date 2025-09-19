package com.priya.authservice.config;

import com.priya.authservice.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;

/**
 * Security configuration for Auth Service.
 *
 * <p>Defines authentication, authorization rules, password encoding,
 * and registers JWT authentication filter.</p>
 */

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
@Hidden
public class SecurityConfig {

    private final JwtAuthenticationFilter gatewayAuthFilter;

    /**
     * Provides a strong password encoder bean using BCrypt.
     *
     * <p>This is used for securely storing and validating user credentials
     * when applicable (e.g., in registration/login flows).</p>
     *
     * @return a {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("Creating BCryptPasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides the authentication manager, integrating with Spring Security.
     *
     * @param configuration Authentication configuration auto-provided by Spring
     * @return the {@link AuthenticationManager} instance
     * @throws Exception if authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        log.debug("Initializing AuthenticationManager from AuthenticationConfiguration");
        return configuration.getAuthenticationManager();
    }

    /**
     * Configures the security filter chain for the Auth Service.
     *
     * <p>Rules:</p>
     * <ul>
     *     <li>Disables CSRF (since JWT is stateless)</li>
     *     <li>Allows public access to <code>/api/auth/**</code> endpoints</li>
     *     <li>Allows public access to Swagger/OpenAPI endpoints</li>
     *     <li>Secures all other endpoints with JWT filter</li>
     * </ul>
     *
     * @param http Spring {@link HttpSecurity} object
     * @return configured {@link SecurityFilterChain}
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Initializing SecurityFilterChain for Auth Service...");

        Objects.requireNonNull(gatewayAuthFilter, "JwtAuthenticationFilter must not be null");

        SecurityFilterChain filterChain = http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Swagger / OpenAPI endpoints
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(gatewayAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

        log.info("SecurityFilterChain successfully initialized for auth-service");
        return filterChain;
    }
}
