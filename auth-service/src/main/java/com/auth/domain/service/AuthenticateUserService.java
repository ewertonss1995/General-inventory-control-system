package com.auth.domain.service;

import com.auth.adapters.in.web.dto.LoginRequest;
import com.auth.adapters.in.web.dto.TokenResponse;
import com.auth.domain.exception.BusinessException;
import com.auth.ports.in.AuthenticateUserUseCase;
import com.auth.ports.in.TokenUseCase;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.adapters.out.database.entity.UserEntity;
import com.auth.adapters.out.database.repository.UserRepository;
import com.auth.ports.in.AuthenticateUserUseCase;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final TokenUseCase tokenUseCase;

    public AuthenticateUserService(UserRepositoryPort userRepositoryPort, 
                                   PasswordEncoder passwordEncoder, 
                                   TokenUseCase tokenUseCase) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.tokenUseCase = tokenUseCase;
    }

    public TokenResponse execute(LoginRequest request) {
        UserEntity user = userRepositoryPort.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas."));

        if (!user.isActive()) {
            throw new BusinessException("Conta de usuário inativa.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Credenciais inválidas.");
        }

        String token = tokenUseCase.generateToken(user);

        return new TokenResponse(token, "Bearer", 7200L);
    }
}