package com.priya.hotelservice.security; // change package per service

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter that intercepts incoming requests to set authentication in the SecurityContext
 * based on forwarded headers from the API Gateway.
 * <p>
 * Expected headers:
 * - X-User-Name: authenticated username
 * - X-User-Role: role of the user (e.g., ROLE_ADMIN, ROLE_USER)
 * <p>
 * This is only applied if no existing authentication is present in the SecurityContext.
 */
@Component
@Slf4j
@Hidden
public class GatewayAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String username = request.getHeader("X-User-Name");
        String role = request.getHeader("X-User-Role"); // expected e.g. ROLE_ADMIN or ROLE_USER

        try {
            if (username != null && role != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singleton(new SimpleGrantedAuthority(role))
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

                log.debug("Authentication set in SecurityContext for user [{}] with role [{}]", username, role);
            } else {
                log.trace("No authentication set. Headers - X-User-Name: {}, X-User-Role: {}", username, role);

            }

            log.info("Forwarded headers: X-User-Name= {} X-User-Role= {}", username, role);
            chain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Error in GatewayAuthFilter for user [{}]", username, ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal authentication error");
        }
    }
}
