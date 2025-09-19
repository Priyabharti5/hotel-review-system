package com.priya.userservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the User Service Spring Boot application.
 * <p>
 * Configures JPA auditing, enables Feign clients for inter-service communication,
 * and sets up logging.
 * </p>
 * <p>
 * Production-grade considerations:
 * - Logging startup info for monitoring.
 * - Auditing enabled for entity creation/modification tracking.
 * - Feign clients ready for microservice communication.
 * </p>
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableFeignClients
@Slf4j
public class UserServiceApplication {

    /**
     * Main method to launch the User Service application.
     * Logs startup message with production-ready awareness.
     *
     * @param args standard Spring Boot application arguments
     */
	public static void main(String[] args) {
        log.info("User Service Application starting ...");
        SpringApplication.run(UserServiceApplication.class, args);
        log.info("User Service Application started successfully");
    }

}
