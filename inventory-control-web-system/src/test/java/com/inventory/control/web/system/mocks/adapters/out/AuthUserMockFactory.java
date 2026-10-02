package com.inventory.control.web.system.mocks.adapters.out;

import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.domain.model.TokenUser;

public final class AuthUserMockFactory {

    public static final String DEFAULT_USERNAME = "eweerton";
    public static final String DEFAULT_EMAIL = "eweerton@email.com";
    public static final String DEFAULT_PASSWORD = "Password123!";
    public static final String DEFAULT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token";
    public static final String DEFAULT_TOKEN_TYPE = "Bearer";
    public static final long DEFAULT_EXPIRES_IN = 3600L;

    private AuthUserMockFactory() {
    }

    public static RegisterUser createRegisterUser() {
        return new RegisterUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    public static LoginUser createLoginUser() {
        return new LoginUser(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public static TokenResponse createTokenResponse() {
        return new TokenResponse(DEFAULT_TOKEN, DEFAULT_TOKEN_TYPE, DEFAULT_EXPIRES_IN);
    }

    public static TokenUser createTokenUser() {
        return new TokenUser(DEFAULT_TOKEN, DEFAULT_TOKEN_TYPE, DEFAULT_EXPIRES_IN);
    }
}