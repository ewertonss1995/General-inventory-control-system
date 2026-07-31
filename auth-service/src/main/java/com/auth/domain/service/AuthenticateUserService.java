package com.auth.domain.service;

import com.auth.adapters.in.web.dto.LoginRequest;
import com.auth.adapters.in.web.dto.TokenResponse;
import com.auth.adapters.out.database.entity.UserEntity;
import com.auth.adapters.out.database.entity.RoleEntity;
import com.auth.domain.exception.BusinessException;
import com.auth.domain.model.Role;
import com.auth.domain.model.User;
import com.auth.ports.in.AuthenticateUserUseCase;
import com.auth.ports.in.TokenUseCase;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.ports.out.PasswordEncoderPort;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticateUserService implements AuthenticateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticateUserService.class);


    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenUseCase tokenUseCase;

    public AuthenticateUserService(UserRepositoryPort userRepositoryPort,
                                    PasswordEncoderPort passwordEncoderPort, 
                                    TokenUseCase tokenUseCase) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenUseCase = tokenUseCase;
    }

    @Override
    public TokenResponse execute(LoginRequest request) {
        log.debug("Iniciando processo de autenticação para: {}", request.usernameOrEmail());
    
        User user = userRepositoryPort.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> {
                    log.warn("Tentativa de autenticação com usuário/e-mail inexistente: {}", request.usernameOrEmail());
                    return new BusinessException("Credenciais inválidas.");
                });

        if (!user.isActive()) {
            log.warn("Tentativa de login bloqueada: a conta do usuário {} está inativa", request.usernameOrEmail());
            throw new BusinessException("Conta de usuário inativa.");
        }

        if (!passwordEncoderPort.matches(request.password(), user.getPassword())) {
            log.warn("Falha de autenticação: senha incorreta para o usuário {}", request.usernameOrEmail());
            throw new BusinessException("Credenciais inválidas.");
        }

        String token = tokenUseCase.generateToken(user);
        
        log.info("Usuário {} autenticado com sucesso", request.usernameOrEmail());
        return new TokenResponse(token, "Bearer", 7200L);
    }
}
