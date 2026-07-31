package com.auth.domain.model;

import com.auth.domain.model.Role;
import java.util.Set;
import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String email;
    private boolean active;
    private String password;
    private Set<Role> roles;

    public User(UUID id, String username, String email, boolean active, String password, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.active = active;
        this.password = password;
        this.roles = roles;
    }

    public User(UUID id, String email, boolean active, String password, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.active = active;
        this.password = password;
        this.roles = roles;
    }

     public User(String username, String email, String password, Set<Role> roles) {
        this.email = email;
        this.active = active;
        this.password = password;
        this.roles = roles;
    }

    public User(String email, boolean active, String password, Set<Role> roles) {
        this.email = email;
        this.active = active;
        this.password = password;
        this.roles = roles;
    }

    public User(String username, String email, String password) {
        this.email = email;
        this.active = active;
        this.password = password;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public boolean isActive() { return active; }
    public String getPassword() { return password; }
    public Set<Role> getRoles() { return roles; }

    public void setPassword(String password) { this.password = password; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

}
