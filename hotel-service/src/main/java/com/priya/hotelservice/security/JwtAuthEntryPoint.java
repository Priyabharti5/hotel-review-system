package com.priya.hotelservice.security;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Entry point for JWT authentication failures.
 * <p>
 * Returns 401 Unauthorized if a user attempts to access a secured endpoint
 * without valid authentication.
 */
@Component
@Slf4j
@Hidden
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences authentication failure handling.
     *
     * @param request       HTTP servlet request.
     * @param response      HTTP servlet response.
     * @param authException Exception that triggered the failure.
     * @throws IOException in case of response writing errors.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("Unauthorized access attempt to [{}] from [{}]", request.getRequestURI(),
                request.getRemoteAddr(), authException);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + authException.getMessage());
    }
}

