package com.priya.authservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication entry point for handling unauthorized access attempts in JWT-secured endpoints.
 * <p>
 * This component is triggered whenever an unauthenticated user or a user with an invalid/expired
 * JWT token tries to access a protected resource. It intercepts the request and returns a
 * standardized JSON error response with HTTP 401 (Unauthorized).
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Log unauthorized access attempts with request details.</li>
 *     <li>Return consistent JSON response body for clients.</li>
 *     <li>Prevent leaking sensitive exception details in the response.</li>
 * </ul>
 */

@Slf4j
@Component
@Hidden
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Commences the authentication entry point process when an unauthorized request is detected.
     *
     * @param request       the HTTP request that triggered the exception
     * @param response      the HTTP response to be sent back to the client
     * @param authException the authentication exception describing the unauthorized attempt
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {

        log.warn("Unauthorized access attempt - Path: [{}], Method: [{}], Error: {}",
                request.getRequestURI(),
                request.getMethod(),
                authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now().toString());
            body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            body.put("message", "Invalid or missing authentication token");
            body.put("path", request.getRequestURI());

            response.getWriter().write(objectMapper.writeValueAsString(body));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

