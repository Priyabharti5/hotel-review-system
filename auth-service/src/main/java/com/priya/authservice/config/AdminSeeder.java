package com.priya.authservice.config;

import com.priya.authservice.entity.Role;
import com.priya.authservice.entity.AuthUser;
import com.priya.authservice.enums.RoleName;
import com.priya.authservice.enums.UserStatus;
import com.priya.authservice.exception.ResourceNotFoundException;
import com.priya.authservice.repository.RoleRepository;
import com.priya.authservice.repository.AuthUserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * {@code AdminSeeder} ensures that a default administrator user exists at startup.
 *
 * <p><b>Production Note:</b> This seeder is intended for <strong>development/testing only</strong>.
 * Never keep default credentials enabled in production; disable this class or change logic accordingly.</p>
 */

//@Profile({"dev", "test"})
@Configuration
@RequiredArgsConstructor
@Slf4j
@Hidden
public class AdminSeeder implements CommandLineRunner {

    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Executes on application startup.
     * Ensures at least one ADMIN user exists.
     *
     * @param args command-line arguments
     */
    @Override
    public void run(String... args) {
        log.debug("Starting AdminSeeder check for default admin user...");

        authUserRepository.findByUsername("admin").ifPresentOrElse(
                existingUser -> log.info("Admin user already exists with userName: {}", existingUser.getUsername()),
                () -> {
                    log.info("No admin user found. Creating default admin user...");

                    // Validate that the ADMIN role exists
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> {
                                log.error("ROLE_ADMIN not found. Cannot seed admin user.");
                                return new ResourceNotFoundException("Admin role not found in database");
                            });

                    // Build the default admin user
                    AuthUser admin = AuthUser.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("admin"))
                            .status(UserStatus.ACTIVE)
                            .role(adminRole)
                            .build();

                    // Persist to DB
                    authUserRepository.save(admin);
                    log.warn("Default admin user created. [username=admin, password=admin]. " +
                            "Change this password immediately in production!");
                }
        );
        log.debug("AdminSeeder execution completed.");
    }
}
