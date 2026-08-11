package com.auth.domain.service;

import com.auth.domain.exception.BusinessException;
import com.auth.ports.in.RegisterUserUseCase;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.ports.out.PasswordEncoderPort;
import com.auth.domain.model.User;
import com.auth.domain.model.Role;

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

    @Override
    public void execute(User user) {
        log.info("Iniciando processo de registro de usuário: " + user.getUsername());
        
        if (userRepositoryPort.existsByUsername(user.getUsername())) {
            log.error("Tentativa de registro com nome de usuário já existente: " + user.getUsername());
            throw new BusinessException("Nome de usuário já está em uso.");
        }

        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            log.error("Tentativa de registro com e-mail já cadastrado: " + user.getEmail());
            throw new BusinessException("E-mail já cadastrado.");
        }

        Role defaultRole = roleRepositoryPort.findByName("ROLE_OPERATOR")
                .orElseThrow(() -> new IllegalStateException("Perfil padrão de operador não encontrado no sistema."));

        user.setPassword(passwordEncoderPort.encode(user.getPassword()));
        user.setRoles(Set.of(defaultRole));

        userRepositoryPort.save(user);
        log.info("Usuário registrado com sucesso: " + user.getUsername());
    }
}
