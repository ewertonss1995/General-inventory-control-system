package com.auth.adapters.in.web;

import com.auth.adapters.in.web.api.AuthApi;
import com.auth.adapters.in.web.dto.LoginRequest;
import com.auth.adapters.in.web.dto.RegisterRequest;
import com.auth.adapters.in.web.dto.TokenResponse;
import com.auth.domain.model.Login;
import com.auth.domain.model.User;
import com.auth.ports.in.LoginUserUseCase;
import com.auth.ports.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController implements AuthApi {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, 
                          LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        log.debug("Recebida requisição de registro para o usuário: {}", request.username());
        
        registerUserUseCase.execute(new User(request.username(), request.email(), request.password()));
        
        log.info("Usuário registrado com sucesso: {}", request.username());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("Recebida tentativa de login para o usuário/email: {}", request.usernameOrEmail());
        
        String response = loginUserUseCase.execute(new Login(request.usernameOrEmail(), request.password()));
        
        log.info("Autenticação realizada com sucesso para o usuário/email: {}", request.usernameOrEmail());
        return ResponseEntity.ok(new TokenResponse(response, "Bearer", 7200L));
    }
}