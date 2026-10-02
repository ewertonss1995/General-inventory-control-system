package com.inventory.control.web.system.mocks.adapters.in;

import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.domain.model.TokenUser;

public final class AuthWebMockFactory {

    public static final String DEFAULT_USERNAME = "eweerton.dev";
    public static final String DEFAULT_EMAIL = "eweerton@inventory.com";
    public static final String DEFAULT_PASSWORD = "Password123!";
    public static final String DEFAULT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0";
    public static final String DEFAULT_TOKEN_TYPE = "Bearer";
    public static final Long DEFAULT_EXPIRES_IN = 3600L;

    private AuthWebMockFactory() {
    }

    public static RegisterUserRequest createValidRegisterUserRequest() {
        return new RegisterUserRequest(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    public static LoginRequest createValidLoginRequest() {
        return new LoginRequest(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public static RegisterUserRequest createInvalidRegisterUserRequest() {
        return new RegisterUserRequest("", "email-invalido", "123");
    }

    public static LoginRequest createInvalidLoginRequest() {
        return new LoginRequest("", "");
    }

    public static RegisterUser createRegisterUserDomain() {
        RegisterUser registerUser = new RegisterUser();
        registerUser.setUsername(DEFAULT_USERNAME);
        registerUser.setEmail(DEFAULT_EMAIL);
        registerUser.setPassword(DEFAULT_PASSWORD);
        return registerUser;
    }

    public static LoginUser createLoginUserDomain() {
        LoginUser loginUser = new LoginUser();
        loginUser.setUsernameOrEmail(DEFAULT_USERNAME);
        loginUser.setPassword(DEFAULT_PASSWORD);
        return loginUser;
    }

    public static TokenUser createTokenUserDomain() {
        return new TokenUser(DEFAULT_TOKEN, DEFAULT_TOKEN_TYPE, DEFAULT_EXPIRES_IN);
    }

    public static TokenResponse createTokenResponse() {
        return new TokenResponse(DEFAULT_TOKEN, DEFAULT_TOKEN_TYPE, DEFAULT_EXPIRES_IN);
    }
}