package com.auth.adapters.out.database.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Entity
@Table(name = "roles")
public class RoleEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    public RoleEntity() {
    }

    public RoleEntity(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return this.name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}