package com.inventory.control.web.system.domain.model;

public class LoginUser {
    private String usernameOrEmail;
    private String password;

    public LoginUser(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }
}
