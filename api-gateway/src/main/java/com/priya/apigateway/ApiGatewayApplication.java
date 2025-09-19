package com.priya.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Entry point for the API Gateway application.
 * <p>
 * This class bootstraps the Spring Boot application and enables service discovery
 * for registering with Eureka or other supported discovery services.
 * <p>
 * Production-grade considerations included:
 * <ul>
 *     <li>Centralized logging using SLF4J</li>
 *     <li>Swagger/OpenAPI documentation can be generated from controllers</li>
 *     <li>Ready for validation annotations on configuration properties</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class ApiGatewayApplication {

    /**
     * Main method to start the API Gateway application.
     *
     * @param args runtime arguments
     */
    public static void main(String[] args) {
        log.info("Starting API Gateway Application...");
        SpringApplication.run(ApiGatewayApplication.class, args);
        log.info("API Gateway Application started successfully.");
    }

}
