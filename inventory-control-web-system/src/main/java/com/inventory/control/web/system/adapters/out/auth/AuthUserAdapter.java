package com.inventory.control.web.system.adapters.out.auth;

import com.inventory.control.web.system.adapters.out.auth.AuthFeignClient;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import com.inventory.control.web.system.ports.out.AuthenticateUserPort;
import com.inventory.control.web.system.ports.out.CreateUserPort;

@Component
public class AuthUserAdapter implements AuthenticateUserPort, CreateUserPort {

    private final AuthFeignClient authFeignClient;

    public AuthUserAdapter(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    @Override
    public void createUser(RegisterUserRequest registerUserRequest) {
        authFeignClient.register(registerUserRequest);
    }

    @Override
    public TokenResponse userLogin(LoginRequest loginRequest) {
        ResponseEntity<TokenResponse> responseToken = authFeignClient.login(loginRequest);
        return responseToken.getBody();
    }
}
