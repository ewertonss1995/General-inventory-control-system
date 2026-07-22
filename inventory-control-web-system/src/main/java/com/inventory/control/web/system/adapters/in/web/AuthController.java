package com.inventory.control.web.system.adapters.in.web;

import com.inventory.control.web.system.adapters.in.web.dto.LoginDto;
import com.inventory.control.web.system.adapters.in.web.dto.RegisterUserDto;
import com.inventory.control.web.system.adapters.in.web.dto.TokenDto;
import com.inventory.control.web.system.adapters.out.auth.AuthFeignClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Web BFF Authentication", description = "Endpoints facilitadores de autenticação para o App Web")
public class AuthController {

    private final AuthFeignClient authFeignClient;

    public AuthController(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    @PostMapping("/register")
    @Operation(summary = "Encaminha o registro de um novo operador para o Auth-Service")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterUserDto request) {
        authFeignClient.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    @Operation(summary = "Encaminha a autenticação para o Auth-Service e devolve o Token JWT")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto request) {
        ResponseEntity<TokenDto> response = authFeignClient.login(request);
        return ResponseEntity.ok(response.getBody());
    }
}