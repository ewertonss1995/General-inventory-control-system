package com.inventory.control.web.system.domain.service.authenticate;

import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.TokenUser;
import com.inventory.control.web.system.ports.in.authenticate.LoginUserUseCase;
import com.inventory.control.web.system.ports.out.AuthenticateFeignPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUserService implements LoginUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(LoginUserService.class);
    
    private final AuthenticateFeignPort authenticateFeignPort;

    public LoginUserService(AuthenticateFeignPort authenticateFeignPort) {
        this.authenticateFeignPort = authenticateFeignPort;
    }

    @Override
    public TokenUser execute(LoginUser loginuser) {
        log.info("Processando caso de uso de autenticação para o usuário: {}", loginuser.getUsernameOrEmail());

        TokenUser tokenUser = authenticateFeignPort.userLogin(loginuser);

        log.info("Usuário {} autenticado com sucesso", loginuser.getUsernameOrEmail());
        return tokenUser;
    }
}
