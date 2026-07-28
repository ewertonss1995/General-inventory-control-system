package com.inventory.control.web.system.adapters.out.auth.authenticate;

import com.inventory.control.web.system.adapters.out.auth.AuthFeignClient;
import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import com.inventory.control.web.system.ports.out.AuthenticateUserPort;

@Component
public class AuthenticateUserAdapter implements AuthenticateUserPort {

    private final AuthFeignClient authFeignClient;

    public AuthenticateUserAdapter(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    @Override
    public TokenResponse userLogin(LoginRequest loginRequest) {
        ResponseEntity<TokenResponse> responseToken = authFeignClient.login(loginRequest);
        return responseToken.getBody();
    }
}
