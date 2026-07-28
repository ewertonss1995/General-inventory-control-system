package com.inventory.control.web.system.domain.service;

import com.inventory.control.web.system.ports.in.RegisterUserUseCase;
import com.inventory.control.web.system.ports.out.CreateUserPort;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;

public class RegisterUserService implements RegisterUserUseCase {
    
    private final CreateUserPort createUserPort;

    public RegisterUserService(CreateUserPort createUserPort) {
        this.createUserPort = createUserPort;
    }

    @Override
    public void execute(RegisterUserRequest userRequest) {
        createUserPort.createUser(userRequest);
    }
}
