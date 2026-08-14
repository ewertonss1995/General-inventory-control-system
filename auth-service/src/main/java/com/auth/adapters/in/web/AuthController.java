package com.auth.adapters.in.web;

import com.auth.adapters.in.web.dto.LoginRequest;
import com.auth.adapters.in.web.dto.RegisterRequest;
import com.auth.adapters.in.web.dto.TokenResponse;
import com.auth.ports.in.LoginUserUseCase;
import com.auth.ports.in.RegisterUserUseCase;
import com.auth.domain.model.User;
import com.auth.domain.model.Login;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, 
                          LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        log.debug("Recebida requisição de registro para o usuário: {}", request.username());
        
        registerUserUseCase.execute(new User(request.username(), request.email(), request.password()));
        
        log.info("Usuário registrado com sucesso: {}", request.username());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("Recebida tentativa de login para o usuário/email: {}", request.usernameOrEmail());
        
        String response = loginUserUseCase.execute(new Login(request.usernameOrEmail(), request.password()));
        
        log.info("Autenticação realizada com sucesso para o usuário/email: {}", request.usernameOrEmail());
        return ResponseEntity.ok(new TokenResponse(response, "Bearer", 7200L));
    }
}
