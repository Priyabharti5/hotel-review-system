package com.priya.authservice.repository;

import com.priya.authservice.entity.AuthUser;
import com.priya.authservice.entity.Role;
import com.priya.authservice.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Repository interface for {@link AuthUser} entity.
 * <p>
 * Provides CRUD operations and custom queries for managing authentication users.
 * </p>
 *
 * <h3>Notes:</h3>
 * <ul>
 *   <li>Use only for database access. Business logic and validation must be handled in the service layer.</li>
 *   <li>Logging should be performed in the service layer, not here. Repositories must remain side-effect free.</li>
 * </ul>
 */
@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    /**
     * Find a user by their unique username.
     *
     * @param username the username to search for (case-sensitive).
     * @return an {@link Optional} containing the {@link AuthUser} if found, empty otherwise.
     */
    Optional<AuthUser> findByUsername(String username);

    /**
     * Check whether a user with the given username already exists.
     *
     * @param username the username to check.
     * @return true if a user exists with the given username, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Retrieve all users having the specified role.
     *
     * @param role the {@link Role} to filter users by.
     * @return list of users assigned to the given role, empty list if none found.
     */
    List<AuthUser> findByRole(Role role);

    /**
     * Retrieve all users having the specified role and status.
     *
     * @param role   the {@link Role} to filter users by.
     * @param status the {@link UserStatus} to filter users by.
     * @return list of users matching the given role and status, empty list if none found.
     */
    List<AuthUser> findByRoleAndStatus(Role role, UserStatus status);

}
