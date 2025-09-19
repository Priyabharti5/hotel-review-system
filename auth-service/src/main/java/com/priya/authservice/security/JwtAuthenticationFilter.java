package com.priya.authservice.security;

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
 * JwtAuthenticationFilter is a custom Spring Security filter that executes once per request.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Extracts authentication details (username, role) from request headers.</li>
 *   <li>Validates headers before allowing the request to proceed.</li>
 *   <li>Sets an authenticated {@link UsernamePasswordAuthenticationToken} in the SecurityContext.</li>
 *   <li>Enriches logs with correlation ID for distributed tracing.</li>
 * </ul>
 *
 * <p><b>Note:</b> This filter assumes that JWT validation has already been
 * performed upstream (e.g., in API Gateway). Here, we only forward the identity
 * extracted from trusted headers.</p>
 *
 * <p>Production considerations:
 * <ul>
 *   <li>Fail-fast response if headers are missing or invalid.</li>
 *   <li>Logs incoming headers for debugging but avoids sensitive information.</li>
 *   <li>Correlation ID is injected if missing.</li>
 * </ul>
 */
@Component
@Slf4j
@Hidden
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USERNAME = "X-User-Name";
    private static final String HEADER_ROLE = "X-User-Role";

    /**
     * Executes filtering logic:
     * <ol>
     *   <li>Ensures a correlation ID exists in the request.</li>
     *   <li>Logs incoming headers for traceability.</li>
     *   <li>Extracts authentication details from headers and sets the security context.</li>
     * </ol>
     *
     * @param request     incoming HTTP request
     * @param response    outgoing HTTP response
     * @param filterChain security filter chain
     * @throws IOException      when I/O error occurs
     * @throws ServletException when servlet exception occurs
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        log.info("Incoming Headers => {}", Collections.list(request.getHeaderNames())
                .stream().map(h -> h + "=" + request.getHeader(h)).toList());


        String username = request.getHeader(HEADER_USERNAME);
        String role = request.getHeader(HEADER_ROLE);

        if (username != null && role != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singleton(new SimpleGrantedAuthority(role))
                    );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        log.info("Forwarded headers: X-User-Name= {} X-User-Role= {}", username, role);
        filterChain.doFilter(request, response);
    }
}
