package com.priya.hotelservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Hotel Service microservice.
 * <p>
 * This service handles all hotel-related operations such as hotel creation,
 * updates, and retrieval. It is a Spring Boot application with JPA auditing
 * enabled and Feign clients for inter-service communication.
 * </p>
 * <p>
 * Production-grade enhancements:
 * <ul>
 *     <li>Logging at application startup</li>
 *     <li>Feign clients enabled for downstream service calls</li>
 *     <li>JPA auditing enabled with custom auditorAware bean</li>
 * </ul>
 * </p>
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableFeignClients
@Slf4j
public class HotelServiceApplication {

    /**
     * Main method to bootstrap the Hotel Service application.
     *
     * @param args runtime arguments
     */
    public static void main(String[] args) {
        log.info("Hotel Service Application starting ...");
        SpringApplication.run(HotelServiceApplication.class, args);
        log.info("Hotel Service Application started successfully.");
    }

}
