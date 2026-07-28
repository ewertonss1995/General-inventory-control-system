package com.inventory.control.web.system.adapters.out.auth;

import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "auth-client", 
    url = "${application.feign.auth-service.url}", 
    configuration = FeignConfig.class)
public interface AuthFeignClient {

    @PostMapping("/auth/login")
    ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request);

    @PostMapping("/auth/register")
    ResponseEntity<Void> register(@RequestBody RegisterUserRequest request);
}
