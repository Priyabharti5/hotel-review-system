package com.priya.authservice.config;

import com.priya.authservice.entity.Role;
import com.priya.authservice.enums.RoleName;
import com.priya.authservice.repository.RoleRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


/**
 * Seeder that ensures {@link RoleName} entries exist in the DB.
 *
 * <p>Runs once at startup to guarantee all roles required for RBAC are present.</p>
 */

//@Profile({"dev", "test"})
@Slf4j
@Component
@RequiredArgsConstructor
@Hidden
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    /**
     * Seeds roles into the database at application startup.
     *
     * <p>For each {@link RoleName}, checks if it already exists in the repository.
     * If not found, creates and persists the role immediately.
     *
     * <p>Uses {@link org.springframework.transaction.annotation.Transactional}
     * to ensure atomicity for seeding operations.
     *
     * @param args application arguments (not used here)
     */
    @Override
    public void run(String... args) {
        log.info("Starting role seeding process...");
        for (RoleName roleName : RoleName.values()) {
            try {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    Role role = Role.builder()
                            .name(roleName)
                            .build();
                    roleRepository.saveAndFlush(role); // flush to ensure persistence
                    log.info("Created new role: {}", roleName);
                } else {
                    log.debug("Skipped creating role: {} (already exists)", roleName);
                }
            } catch (Exception ex) {
                log.error("Failed to seed role {} due to error: {}", roleName, ex.getMessage(), ex);
            }
        }

        log.info("Role seeding process completed.");
    }

}
