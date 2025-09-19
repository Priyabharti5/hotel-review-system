package com.priya.apigateway.config;

import com.priya.apigateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Security configuration for API Gateway.
 * <p>
 * This class configures JWT-based authentication and authorization for
 * incoming requests. Auth endpoints (register, login) and actuator endpoints
 * are publicly accessible, while all other endpoints require a valid JWT.
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    /**
     * Defines the security filter chain for WebFlux applications.
     *
     * @param http the {@link ServerHttpSecurity} instance
     * @return configured {@link SecurityWebFilterChain}
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        SecurityWebFilterChain filterChain = http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/auth/**").permitAll()
                        // allow login/register
                        .pathMatchers(HttpMethod.POST, "/api/auth/user/register").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/hotel-manager-user/register").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        .anyExchange().authenticated()
                )
                .authenticationManager(authentication -> {
                    String token = authentication.getCredentials().toString();
                    return Mono.justOrEmpty(validateToken(token));
                })
                .securityContextRepository(new JwtSecurityContextRepository(this::validateToken))
                .build();

        log.info("SecurityFilterChain successfully initialized for auth-service");
        return filterChain;
    }

    /**
     * Validates the given JWT token and extracts authentication details.
     *
     * @param token JWT token
     * @return {@link AbstractAuthenticationToken} if valid, otherwise {@code null}
     */
    private AbstractAuthenticationToken validateToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)){
                log.warn("JWT validation failed: token is invalid or expired");
                return null;
            }

            String username = jwtUtil.extractRole(token);
            String role = jwtUtil.extractUsername(token);

            log.debug("JWT validated for user [{}] with role [{}]", username, role);

            return new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    Collections.singleton(new SimpleGrantedAuthority(role))
            );
        } catch (Exception e) {
            log.error("Exception while validating JWT: {}", e.getMessage(), e);
            return null; // invalid token
        }
    }
}