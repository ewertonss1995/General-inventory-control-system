package com.inventory.control.web.system.domain.service.authenticate;

import com.inventory.control.web.system.ports.in.authenticate.RegisterUserUseCase;
import com.inventory.control.web.system.ports.out.AuthenticateFeignPort;
import com.inventory.control.web.system.domain.model.RegisterUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterUserService implements RegisterUserUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);

    private final AuthenticateFeignPort authenticateFeignPort;

    public RegisterUserService(AuthenticateFeignPort authenticateFeignPort) {
        this.authenticateFeignPort = authenticateFeignPort;
    }

    @Override
    public void execute(RegisterUser registerUser) {
        log.info("Iniciando caso de uso de registro para o usuário: {}", registerUser.getUsername());

        authenticateFeignPort.createUser(registerUser);

        log.info("Usuário {} registrado com sucesso no sistema", registerUser.getUsername());
    }
}
