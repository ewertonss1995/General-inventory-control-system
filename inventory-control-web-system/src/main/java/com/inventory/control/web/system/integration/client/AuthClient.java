package com.inventory.control.web.system.infrastructure.integration.client;

import com.inventory.control.web.system.application.dto.LoginDto;
import com.inventory.control.web.system.application.dto.RegisterUserDto;
import com.inventory.control.web.system.application.dto.TokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Aponta para a chave configurada no application.yml
@FeignClient(name = "auth-client", url = "${integrations.auth-service.url}")
public interface AuthClient {

    @PostMapping("/auth/register")
    ResponseEntity<Void> register(@RequestBody RegisterUserDto request);

    @PostMapping("/auth/login")
    ResponseEntity<TokenDto> login(@RequestBody LoginDto request);
}