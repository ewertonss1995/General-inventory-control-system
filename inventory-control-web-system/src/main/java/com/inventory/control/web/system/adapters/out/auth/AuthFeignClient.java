package com.inventory.control.web.system.adapters.out.auth;

import com.inventory.control.web.system.adapters.in.web.dto.LoginDto;
import com.inventory.control.web.system.adapters.in.web.dto.RegisterUserDto;
import com.inventory.control.web.system.adapters.in.web.dto.TokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-client", url = "${integrations.auth-service.url}")
public interface AuthFeignClient {

    @PostMapping("/auth/register")
    ResponseEntity<Void> register(@RequestBody RegisterUserDto request);

    @PostMapping("/auth/login")
    ResponseEntity<TokenDto> login(@RequestBody LoginDto request);

    @PostMapping("/auth/validate")
    boolean validate(@RequestBody String token);
}