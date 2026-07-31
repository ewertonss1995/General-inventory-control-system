package com.auth.adapters.in.web;

import com.auth.adapters.in.web.dto.LoginRequest;
import com.auth.adapters.in.web.dto.RegisterRequest;
import com.auth.adapters.in.web.dto.TokenResponse;
import com.auth.ports.in.AuthenticateUserUseCase;
import com.auth.ports.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, 
                          AuthenticateUserUseCase authenticateUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        log.debug("Recebida requisição de registro para o usuário: {}", request.username());
        
        registerUserUseCase.execute(request);
        
        log.info("Usuário registrado com sucesso: {}", request.username());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("Recebida tentativa de login para o usuário/email: {}", request.usernameOrEmail());
        
        TokenResponse response = authenticateUserUseCase.execute(request);
        
        log.info("Autenticação realizada com sucesso para o usuário/email: {}", request.usernameOrEmail());
        return ResponseEntity.ok(response);
    }
}
