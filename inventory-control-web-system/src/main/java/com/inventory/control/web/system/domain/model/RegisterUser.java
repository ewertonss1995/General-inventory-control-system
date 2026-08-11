package com.inventory.control.web.system.domain.model;

public class RegisterUser {
    private String username;
    private String email;
    private String password;

    public RegisterUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
