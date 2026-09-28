package com.inventory.control.web.system.adapters.in.web;

import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.domain.model.TokenUser;
import com.inventory.control.web.system.ports.in.authenticate.LoginUserUseCase;
import com.inventory.control.web.system.ports.in.authenticate.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final LoginUserUseCase loginUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(LoginUserUseCase loginUserUseCase, RegisterUserUseCase registerUserUseCase) {
        this.loginUserUseCase = loginUserUseCase;
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody RegisterUserRequest request) {
        log.info("Iniciando processo de registro de usuário: " + request.username());
        registerUserUseCase.execute(mapToRegisterUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Iniciando processo de login de usuário: " + request.usernameOrEmail());
        TokenUser response = loginUserUseCase.execute(mapToLoginUser(request));
        return ResponseEntity.ok(mapToTokenResponse(response));
    }

    private RegisterUser mapToRegisterUser(RegisterUserRequest request) {
        return new RegisterUser(request.username(), request.email(), request.password());
    }

    private LoginUser mapToLoginUser(LoginRequest request) {
        return new LoginUser(request.usernameOrEmail(), request.password());
    }

    private TokenResponse mapToTokenResponse(TokenUser tokenUser) {
        return new TokenResponse(tokenUser.accessToken(), tokenUser.tokenType(), tokenUser.expiresInSeconds());
    }
}
