package com.inventory.control.web.system.adapters.out.auth;

import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.RegisterUser;
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
    ResponseEntity<TokenResponse> login(@RequestBody LoginUser loginUser);

    @PostMapping("/auth/register")
    ResponseEntity<Void> register(@RequestBody RegisterUser registerUser);
}
