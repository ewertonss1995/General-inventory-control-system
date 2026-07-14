package com.auth.application.usecase;

import com.auth.application.dto.LoginRequest;
import com.auth.application.dto.TokenResponse;
import com.auth.domain.exception.BusinessException;
import com.auth.domain.service.TokenService;
import com.auth.infrastructure.persistence.jpa.entity.UserEntity;
import com.auth.infrastructure.persistence.jpa.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthenticateUserUseCase(UserRepository userRepository, 
                                   PasswordEncoder passwordEncoder, 
                                   TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public TokenResponse execute(LoginRequest request) {
        // Busca flexível por Username ou E-mail
        UserEntity user = userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas."));

        if (!user.isActive()) {
            throw new BusinessException("Conta de usuário inativa.");
        }

        // Valida se a senha enviada bate com o hash salvo no banco (BCrypt)
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Credenciais inválidas.");
        }

        String token = tokenService.generateToken(user);

        return new TokenResponse(token, "Bearer", 7200L);
    }
}