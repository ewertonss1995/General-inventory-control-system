package com.auth.domain.service;

import com.auth.adapters.in.web.dto.RegisterRequest;
import com.auth.domain.exception.BusinessException;
import com.auth.ports.in.RegisterUserUseCase;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.ports.out.PasswordEncoderPort;
import com.auth.domain.model.User;
import com.auth.domain.model.Role;

import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterUserService implements RegisterUserUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);

    private final RoleRepositoryPort roleRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public RegisterUserService(RoleRepositoryPort roleRepositoryPort, 
                                UserRepositoryPort userRepositoryPort, 
                                PasswordEncoderPort passwordEncoderPort) {
        this.roleRepositoryPort = roleRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Transactional
    @Override
    public void execute(RegisterRequest request) {
        log.info("Iniciando processo de registro de usuário: " + request.username());
        
        if (userRepositoryPort.existsByUsername(request.username())) {
            log.error("Tentativa de registro com nome de usuário já existente: " + request.username());
            throw new BusinessException("Nome de usuário já está em uso.");
        }

        if (userRepositoryPort.existsByEmail(request.email())) {
            log.error("Tentativa de registro com e-mail já cadastrado: " + request.email());
            throw new BusinessException("E-mail já cadastrado.");
        }

        Role defaultRole = roleRepositoryPort.findByName("ROLE_OPERATOR")
                .orElseThrow(() -> new IllegalStateException("Perfil padrão de operador não encontrado no sistema."));

        User user = new User(
            request.username(), 
            request.email(), 
            passwordEncoderPort.encode(request.password()), 
            Set.of(defaultRole));

        userRepositoryPort.save(user);
        log.info("Usuário registrado com sucesso: " + request.username());
    }
}
