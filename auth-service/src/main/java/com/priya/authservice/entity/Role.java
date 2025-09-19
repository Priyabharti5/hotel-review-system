package com.priya.authservice.entity;

import com.priya.authservice.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    // Enum Roles: ROLE_ADMIN, ROLE_USER, ROLE_HOTEL_MANAGER
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, updatable = false)
    private RoleName name;
}
