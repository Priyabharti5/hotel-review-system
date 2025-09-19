package com.priya.authservice.repository;

import com.priya.authservice.entity.Role;
import com.priya.authservice.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Repository interface for {@link Role} entity.
 * <p>
 * Provides CRUD operations and queries for managing user roles.
 * </p>
 *
 * <h3>Notes:</h3>
 * <ul>
 *   <li>Repositories must only define persistence queries. Do not add business logic here.</li>
 *   <li>Logging belongs in the service layer that consumes this repository.</li>
 * </ul>
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find a role by its {@link RoleName}.
     *
     * @param name the role name enum (e.g., ROLE_ADMIN, ROLE_USER).
     * @return an {@link Optional} containing the {@link Role} if found, empty otherwise.
     */
    Optional<Role> findByName(RoleName name);
}
