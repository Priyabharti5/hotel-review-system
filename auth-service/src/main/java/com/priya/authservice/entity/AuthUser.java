package com.priya.authservice.entity;

import com.priya.authservice.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auth_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true, updatable = false)
    private String username; // login name - studentId or instructorId

    @Column(name = "password", nullable = false)
    private String password; // BCrypt hashed

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status; // default status

    @ManyToOne(fetch = FetchType.EAGER)   // single role per user
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

}
