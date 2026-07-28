package com.inventory.control.web.system.domain.service;

import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.ports.in.AuthenticateUserUseCase;
import com.inventory.control.web.system.ports.out.AuthenticateUserPort;

public class AuthenticateUserService implements AuthenticateUserUseCase {
    
    private final AuthenticateUserPort authenticateUserPort;

    public AuthenticateUserService(AuthenticateUserPort authenticateUserPort) {
        this.authenticateUserPort = authenticateUserPort;
    }

    @Override
    public TokenResponse execute(LoginRequest loginRequest) {
        return authenticateUserPort.userLogin(loginRequest);
    }
}