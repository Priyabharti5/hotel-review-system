package com.priya.apigateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import java.util.Collections;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange); // No token → continue (public endpoints may exist)
        }

        String token = authHeader.substring(7);

        boolean valid = jwtUtil.validateToken(token);

        if (!valid) {
            log.warn("Invalid or expired token");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extract claims
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        log.debug("api-gateway: username = {}", username);
        log.debug("api-gateway: role = {}", role);

        // Forward downstream → Services trust Gateway
        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Name", username)
                        .header("X-User-Role", role)
                        .build())
                .build();

        log.info("Forwarding request with headers: X-User-Name={}, X-User-Role={}", username, role);

        // Reactive SecurityContext for Gateway-internal checks
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );

        SecurityContextImpl context = new SecurityContextImpl(authentication);

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
    }

    @Override
    public int getOrder() {
        return -1; // execute before other filters
    }

}
