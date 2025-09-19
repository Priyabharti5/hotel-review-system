package com.priya.userservice.config;

import com.priya.userservice.security.GatewayAuthFilter;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for Hotel Service.
 *
 * <p>Integrates with Gateway for JWT authentication propagation,
 * enables method-level security, and secures all endpoints except
 * explicitly whitelisted ones.</p>
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
@Hidden
public class SecurityConfig {

    private final GatewayAuthFilter gatewayAuthFilter;

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
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the {@link AuthenticationManager} bean for authentication use.
     *
     * @param configuration the authentication configuration injected by Spring
     * @return the authentication manager instance
     * @throws Exception if authentication manager initialization fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Configures the security filter chain for the User Service.
     *
     * <ul>
     *     <li>Disables CSRF protection (not needed for stateless JWT-based APIs).</li>
     *     <li>Permits health, info, and Swagger endpoints for observability and documentation.</li>
     *     <li>All other endpoints require a valid JWT propagated via {@link GatewayAuthFilter}.</li>
     * </ul>
     *
     * @param http the HttpSecurity builder
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Initializing SecurityFilterChain for user-service"); // rename per service

        SecurityFilterChain filterChain =  http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // expose health & docs if needed
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info")
                        .permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        // everything else needs an authenticated context from gateway
                        .anyRequest().authenticated()
                )
                .addFilterBefore(gatewayAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

        log.info("SecurityFilterChain successfully initialized for user-service");
        return filterChain;
    }
}
