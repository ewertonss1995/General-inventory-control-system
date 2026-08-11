package com.auth.domain.model;

public class Login {
    private String usernameOrEmail;
    private String password;

    public Login(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public String getUsernameOrEmail(){
        return this.usernameOrEmail;
    }

    public String getPassword() {
        return this.password;
    }
}