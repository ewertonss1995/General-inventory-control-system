package com.inventory.control.web.system.domain.service;

import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.ports.in.AuthenticateUserUseCase;
import com.inventory.control.web.system.ports.out.AuthenticateUserPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticateUserService implements AuthenticateUserUseCase {
    
    private final AuthenticateUserPort authenticateUserPort;

    public AuthenticateUserService(AuthenticateUserPort authenticateUserPort) {
        this.authenticateUserPort = authenticateUserPort;
    }

    @Override
    public TokenResponse execute(LoginRequest loginRequest) {
        log.info("Iniciando processo de registro de usuário: " + loginRequest.usernameOrEmail());
        try {
            return authenticateUserPort.userLogin(loginRequest);
        } catch (Exception e) {
            log.error("Erro durante login de usuário: " + loginRequest.usernameOrEmail() + " ERRO: " + e.getMessage() );
            throw e;
        }
    }
}
