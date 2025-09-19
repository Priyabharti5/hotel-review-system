package com.priya.authservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the Authentication and Authorization Service.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Bootstraps the Spring Boot application.</li>
 *   <li>Enables Feign clients for inter-service communication.</li>
 *   <li>Exposes Bean definitions for validation (JSR-380 / Jakarta Validation).</li>
 *   <li>Configures Swagger/OpenAPI metadata for API documentation.</li>
 * </ul>
 *
 * <p>
 * This service manages user registration, authentication, JWT issuance,
 * and role-based authorization across the system.
 * </p>
 */

@Slf4j
@OpenAPIDefinition(
        info = @Info(
                title = "Auth Service API",
                version = "1.0.0",
                description = "Authentication and Authorization microservice for managing users, roles, and JWT tokens.",
                contact = @Contact(
                        name = "Priya Bharti",
                        email = "priyabharti315@gmail.com",
                        url = "https://github.com/Priyabharti5"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
@SpringBootApplication
@EnableFeignClients
public class AuthServiceApplication {

    /**
     * Main method that bootstraps the Spring Boot application.
     *
     * @param args application startup arguments
     */
    public static void main(String[] args) {
        log.info("Starting Auth Service Application...");

        SpringApplication.run(AuthServiceApplication.class, args);

        log.info("Auth Service Application started successfully.");
    }

}
