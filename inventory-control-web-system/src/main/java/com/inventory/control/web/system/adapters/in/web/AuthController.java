package com.inventory.control.web.system.adapters.in.web;

import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.domain.model.TokenUser;
import com.inventory.control.web.system.ports.in.authenticate.LoginUserUseCase;
import com.inventory.control.web.system.ports.in.authenticate.RegisterUserUseCase;
import com.inventory.control.web.system.adapters.in.web.mapper.AuthenticateMapper;
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

    private final AuthenticateMapper mapper;
    private final LoginUserUseCase loginUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(AuthenticateMapper mapper, LoginUserUseCase loginUserUseCase, RegisterUserUseCase registerUserUseCase) {
        this.mapper = mapper;
        this.loginUserUseCase = loginUserUseCase;
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody RegisterUserRequest request) {
        log.info("Iniciando processo de registro de usuário: " + request.username());
        registerUserUseCase.execute(mapper.toRegisterUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Iniciando processo de login de usuário: " + request.usernameOrEmail());
        TokenUser response = loginUserUseCase.execute(mapper.toLoginUser(request));
        return ResponseEntity.ok(mapper.toTokenResponse(response));
    }
}
