package com.auth.infrastructure.controller;

import com.auth.application.dto.LoginRequest;
import com.auth.application.dto.RegisterRequest;
import com.auth.application.dto.TokenResponse;
import com.auth.application.usecase.AuthenticateUserUseCase;
import com.auth.application.usecase.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints de login e registro de operadores de estoque")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, 
                          AuthenticateUserUseCase authenticateUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Registra um novo operador de estoque", description = "Cria uma conta e atribui automaticamente a regra básica de operador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erros de validação ou payload incorreto"),
        @ApiResponse(responseCode = "422", description = "Dados duplicados ou regra de negócio violada")
    })
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        registerUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    @Operation(summary = "Realiza o login de um operador", description = "Valida as credenciais e retorna um token JWT assinado com RSA-256.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erros de validação de payload"),
        @ApiResponse(responseCode = "401", description = "Credenciais incorretas")
    })
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authenticateUserUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}