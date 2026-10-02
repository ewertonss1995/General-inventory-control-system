package com.inventory.control.web.system.mocks.domain.service.authenticate;

import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.domain.model.TokenUser;

public final class AuthDomainMockFactory {

    public static final String DEFAULT_USERNAME = "eweerton";
    public static final String DEFAULT_EMAIL = "eweerton@inventory.com";
    public static final String DEFAULT_USERNAME_OR_EMAIL = "eweerton@inventory.com";
    public static final String DEFAULT_PASSWORD = "SecretPassword123!";
    public static final String DEFAULT_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ";
    public static final String DEFAULT_TOKEN_TYPE = "Bearer";
    public static final long DEFAULT_EXPIRES_IN = 3600L;

    private AuthDomainMockFactory() {
    }

    public static RegisterUser createRegisterUser() {
        return new RegisterUser(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    public static LoginUser createLoginUser() {
        return new LoginUser(DEFAULT_USERNAME_OR_EMAIL, DEFAULT_PASSWORD);
    }

    public static TokenUser createTokenUser() {
        return new TokenUser(DEFAULT_ACCESS_TOKEN, DEFAULT_TOKEN_TYPE, DEFAULT_EXPIRES_IN);
    }
}