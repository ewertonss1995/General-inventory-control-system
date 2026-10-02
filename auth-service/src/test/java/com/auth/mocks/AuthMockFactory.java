package com.auth.mocks;

import com.auth.adapters.in.web.dto.LoginRequest;
import com.auth.adapters.in.web.dto.RegisterRequest;
import com.auth.adapters.in.web.dto.TokenResponse;
import com.auth.domain.model.Login;
import com.auth.domain.model.Role;
import com.auth.domain.model.User;

import java.util.Set;
import java.util.UUID;

public final class AuthMockFactory {

    private AuthMockFactory() {}

    public static RegisterRequest createRegisterRequest() {
        return new RegisterRequest(
                "usuario_teste",
                "usuario@email.com",
                "senha123"
        );
    }

    public static RegisterRequest createInvalidRegisterRequest() {
        return new RegisterRequest(
                "usr",             // Username curto (< 4 chars)
                "email-invalido",  // Formato de email inválido
                "123"              // Senha curta (< 6 chars)
        );
    }

    public static LoginRequest createLoginRequest() {
        return new LoginRequest(
                "usuario_teste",
                "senha123"
        );
    }

    public static LoginRequest createInvalidLoginRequest() {
        return new LoginRequest(
                "", // Blank
                ""  // Blank
        );
    }

    public static TokenResponse createTokenResponse() {
        return new TokenResponse(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token.mock",
                "Bearer",
                7200L
        );
    }

    public static User createUser() {
        return createUser("usuario_teste", "usuario@email.com", "senha123", true);
    }

    public static User createUser(String username, String email, String password, boolean active) {
        Role defaultRole = new Role(UUID.randomUUID(), "ROLE_USER");
        return new User(
                UUID.randomUUID(),
                username,
                email,
                active,
                password,
                Set.of(defaultRole)
        );
    }

    public static Login createLogin() {
        return new Login(
                "usuario_teste",
                "senha123"
        );
    }
}