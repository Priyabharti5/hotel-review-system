package com.priya.userservice.entity;

import com.priya.userservice.enums.UserStatus;
import com.priya.userservice.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a User entity in the Hotel Review System.
 * Stores user details including name, contact info, and bio.
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "userId"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User extends BaseEntity {

    /**
     * Primary key of the user (internal use).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * Unique business user ID (10-digit string).
     */
    @Column(name = "user_id", nullable = false, unique = true, length = 10, updatable = false)
    private String userId;

    /**
     * Full name of the user.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Mobile number (must be unique).
     */
    @Column(name = "mobile", nullable = false, unique = true)
    private String mobile;

    /**
     * Email address (must be unique).
     */
    @Column(name = "email", unique = true)
    private String email;

    /**
     * Optional short bio or about section.
     */
    @Column(name = "about")
    private String about;

    /**
     * Current status of the user.
     * Helps in soft delete and account management.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    /**
     * Type of the user in the system.
     * NORMAL -> hotel guest/reviewer
     * HOTEL_MANAGER -> manages hotels
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;
}
