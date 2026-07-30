package com.inventory.control.web.system.domain.service;

import com.inventory.control.web.system.ports.in.RegisterUserUseCase;
import com.inventory.control.web.system.ports.out.CreateUserPort;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterUserService implements RegisterUserUseCase {
    
    private final CreateUserPort createUserPort;

    public RegisterUserService(CreateUserPort createUserPort) {
        this.createUserPort = createUserPort;
    }

    @Override
    public void execute(RegisterUserRequest userRequest) {
        log.info("Iniciando processo de registro de usuário: " + userRequest.username());
        try {
            createUserPort.createUser(userRequest);
        } catch (Exception e) {
            log.error("Erro durante login de usuário: " + userRequest.username() + " ERRO: " + e.getMessage());
            throw e;
        }
    }
}
