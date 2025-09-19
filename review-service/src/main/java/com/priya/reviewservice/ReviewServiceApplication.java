package com.priya.reviewservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the Review Service application.
 * <p>
 * This service handles all operations related to hotel reviews.
 * <ul>
 *     <li>Exposes REST endpoints for review management.</li>
 *     <li>Uses Feign clients to communicate with other microservices.</li>
 *     <li>Supports JPA auditing for created/modified timestamps and users.</li>
 * </ul>
 * </p>
 * <p>
 * Logging is enabled at startup to confirm application initialization.
 * </p>
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableFeignClients
@Slf4j
public class ReviewServiceApplication {

    /**
     * Application main method.
     * Initializes and starts the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        log.info("Starting Review Service application...");
        SpringApplication.run(ReviewServiceApplication.class, args);
        log.info("Review Service started successfully.");
    }

}
