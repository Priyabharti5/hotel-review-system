package com.priya.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * ServerSecurityContextRepository implementation for JWT-based authentication.
 * <p>
 * Responsible for extracting the JWT from the Authorization header, validating it,
 * and populating the SecurityContext with the resulting Authentication.
 * Stateless; does not persist SecurityContext.
 */
@Slf4j
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

    private final Function<String, AbstractAuthenticationToken> tokenValidator;

    /**
     * Constructs a JwtSecurityContextRepository with the given token validator.
     *
     * @param tokenValidator a function that validates a JWT and returns an Authentication token,
     *                       or null if invalid
     */
    public JwtSecurityContextRepository(Function<String, AbstractAuthenticationToken> tokenValidator) {
        if (tokenValidator == null) {
            throw new IllegalArgumentException("tokenValidator must not be null");
        }
        this.tokenValidator = tokenValidator;
    }

    /**
     * Stateless implementation: saving the SecurityContext is not supported.
     *
     * @param exchange the current server exchange
     * @param context  the security context to save
     * @return Mono.empty() always
     */
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        log.debug("Attempt to save SecurityContext ignored (stateless)");
        return Mono.empty(); // no stateful saving
    }

    /**
     * Loads the SecurityContext from the JWT Authorization header.
     *
     * @param exchange the current server exchange
     * @return Mono of SecurityContext if valid JWT is found, otherwise Mono.empty()
     */
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            AbstractAuthenticationToken authentication = tokenValidator.apply(authHeader);
            if (authentication != null) {
                return Mono.just(new SecurityContextImpl(authentication));
            }
        }
        log.warn("ServerWebExchange or request headers are null");
        return Mono.empty();
    }
}
