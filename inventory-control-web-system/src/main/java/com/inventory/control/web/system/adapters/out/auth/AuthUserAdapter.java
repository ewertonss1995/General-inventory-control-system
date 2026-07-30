package com.inventory.control.web.system.adapters.out.auth;

import com.inventory.control.web.system.adapters.out.auth.AuthFeignClient;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import com.inventory.control.web.system.ports.out.AuthenticateUserPort;
import com.inventory.control.web.system.ports.out.CreateUserPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthUserAdapter implements AuthenticateUserPort, CreateUserPort {

    private final AuthFeignClient authFeignClient;

    public AuthUserAdapter(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    @Override
    public void createUser(RegisterUserRequest registerUserRequest) {
        log.info("Iniciando processo de registro de usuário: " + registerUserRequest.username());
        try {
            authFeignClient.register(registerUserRequest);
        } catch (Exception e) {
            log.error("Erro durante registro de usuário: " + 
            registerUserRequest.username() + " ERRO: " + e.getMessage() );

            throw e;
        }
    }

    @Override
    public TokenResponse userLogin(LoginRequest loginRequest) {
        log.info("Iniciando processo de registro de usuário: " + loginRequest.usernameOrEmail());
        ResponseEntity<TokenResponse> responseToken;
        try {
            responseToken = authFeignClient.login(loginRequest);
        } catch (Exception e) {
            log.error("Erro durante login de usuário: " + loginRequest.usernameOrEmail() + " ERRO: " + e.getMessage() );
            throw e;
        }
        
        return responseToken.getBody();
    }
}
