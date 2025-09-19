package com.priya.userservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * OpenAPI (Swagger) configuration.
 *
 * <p>Enabled only under {@code dev} profile.
 * Provides API documentation with JWT bearer authentication support.
 * This must remain disabled in production environments.</p>
 */
@OpenAPIDefinition(
        info = @Info(
                title = "User Service API",
                version = "1.0.0",
                description = "API documentation for User Service",
                contact = @Contact(
                        name = "Priya",
                        email = "priyabharti315@gmail.com",
                        url = "https://github.com/Priyabharti5"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
@Profile("dev")
@Configuration
@Slf4j
public class OpenApiConfig {
    /**
     * Configures OpenAPI documentation with JWT bearer authentication.
     *
     * @return customized {@link OpenAPI} bean
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        log.info("Initializing OpenAPI configuration with JWT Bearer authentication (profile=dev)");

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}