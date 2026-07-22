package com.inventory.control.web.system.adapters.out.auth;

import com.inventory.control.web.system.adapters.out.auth.AuthFeignClient;
import com.inventory.control.web.system.ports.out.AuthClientPort;
import com.inventory.control.web.system.adapters.in.web.dto.LoginDto;
import com.inventory.control.web.system.adapters.in.web.dto.TokenDto;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Component
public class AuthClientAdapter implements AuthClientPort {

    private final AuthFeignClient authFeignClient;

    public AuthClientAdapter(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    @Override
    public TokenDto authenticate(LoginDto loginDto) {
        ResponseEntity<TokenDto> responseToken = authFeignClient.login(loginDto);
        return responseToken.getBody();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return authFeignClient.validate(token);
        } catch (Exception e) {
            return false;
        }
    }
}