package com.inventory.control.web.system.adapters.out.auth;

import com.inventory.control.web.system.adapters.in.web.mapper.AuthenticateMapper;
import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.domain.model.TokenUser;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;

import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import com.inventory.control.web.system.ports.out.AuthenticateFeignPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuthUserAdapter implements AuthenticateFeignPort {

    private static final Logger log = LoggerFactory.getLogger(AuthUserAdapter.class);

    private final AuthenticateMapper mapper;
    private final AuthFeignClient authFeignClient;

    public AuthUserAdapter(AuthenticateMapper mapper, AuthFeignClient authFeignClient) {
        this.mapper = mapper;
        this.authFeignClient = authFeignClient;
    }

    @Override
    public void createUser(RegisterUser registerUser) {
        log.debug("Iniciando processo de registro de usuário: {} no serviço de autenticação",
                registerUser.getUsername());
        authFeignClient.register(registerUser);

    }

    @Override
    public TokenUser userLogin(LoginUser loginUser) {
        log.debug("Iniciando processo de login de usuário: {} no serviço de autenticação",
                loginUser.getUsernameOrEmail());
        ResponseEntity<TokenResponse> responseToken = authFeignClient.login(loginUser);
        return mapper.toTokenUser(responseToken.getBody());
    }

}
