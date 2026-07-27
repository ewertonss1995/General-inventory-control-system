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
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("Iniciando processo de autenticação: " + request.usernameOrEmail());
        
        UserEntity user = userRepositoryPort.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas."));

        if (!user.isActive()) {
            log.error("A conta do usuário não esta ativa: " + request.usernameOrEmail());
            throw new BusinessException("Conta de usuário inativa: " + request.usernameOrEmail());
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.error("Credenciais inválidas, verifique sua senha!");
            throw new BusinessException("Credenciais inválidas:" + request.usernameOrEmail());
        }

        String token = tokenUseCase.generateToken(user);
        log.info("Usuário autenticado com sucesso!");
        return new TokenResponse(token, "Bearer", 7200L);
    }
}