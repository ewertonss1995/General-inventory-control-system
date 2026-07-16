package com.inventory.control.web.system.infrastructure.controller;

import com.inventory.control.web.system.application.dto.LoginDto;
import com.inventory.control.web.system.application.dto.RegisterUserDto;
import com.inventory.control.web.system.application.dto.TokenDto;
import com.inventory.control.web.system.infrastructure.integration.client.AuthClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/web/auth")
@Tag(name = "Web BFF Authentication", description = "Endpoints facilitadores de autenticação para o App Web")
public class BffAuthController {

    private final AuthClient authClient;

    public BffAuthController(AuthClient authClient) {
        this.authClient = authClient;
    }

    @PostMapping("/register")
    @Operation(summary = "Encaminha o registro de um novo operador para o Auth-Service")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterUserDto request) {
        authClient.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    @Operation(summary = "Encaminha a autenticação para o Auth-Service e devolve o Token JWT")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto request) {
        ResponseEntity<TokenDto> response = authClient.login(request);
        return ResponseEntity.ok(response.getBody());
    }
}