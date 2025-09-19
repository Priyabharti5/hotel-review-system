package com.priya.userservice.repository;

import com.priya.userservice.entity.User;
import com.priya.userservice.enums.UserStatus;
import com.priya.userservice.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link User} entity.
 * <p>
 * Provides CRUD operations and custom finder methods for accessing User data.
 * This interface is a part of the persistence layer and should be used only
 * from service classes (business layer) rather than directly from controllers.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by 10-digit unique userId.
     *
     * @param userId unique user identifier (business key, not DB primary key)
     * @return Optional containing the User if found, otherwise empty
     */
    Optional<User> findByUserId(String userId);

    /**
     * Check if a user with the given mobile number already exists.
     *
     * @param mobile user's mobile number
     * @return true if a user exists, false otherwise
     */
    boolean existsByMobile(String mobile);

    /**
     * Check if a user with the given email address already exists.
     *
     * @param email user's email address
     * @return true if a user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by mobile number.
     *
     * @param mobile user's mobile number
     * @return Optional containing the User if found, otherwise empty
     */
    Optional<User> findByMobile(String mobile);

    /**
     * Find user by email.
     *
     * @param email user's email
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all users by status.
     *
     * @param status user status (e.g. ACTIVE, INACTIVE, BLOCKED)
     * @return list of users with the given status
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Find all users by type and status.
     *
     * @param userType type of the user (e.g. ADMIN, CUSTOMER, HOTEL_OWNER)
     * @param status   current status of the user
     * @return list of users matching the type and status
     */
    List<User> findByUserTypeAndStatus(UserType userType, UserStatus status);

    /**
     * Checks if a user exists for the given userId.
     * Useful for validation before performing actions.
     *
     * @param userId Business User ID.
     * @return true if user exists, false otherwise.
     */
    boolean existsByUserId(String userId);

}
