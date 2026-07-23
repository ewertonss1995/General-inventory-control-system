package com.auth.domain.service;

import com.auth.adapters.in.web.dto.RegisterRequest;
import com.auth.domain.exception.BusinessException;
import com.auth.ports.in.RegisterUserUseCase;
import com.auth.adapters.out.database.entity.RoleEntity;
import com.auth.adapters.out.database.entity.UserEntity;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.ports.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public class RegisterUserService implements RegisterUserUseCase {

    private final RoleRepositoryPort roleRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(RoleRepositoryPort roleRepositoryPort, UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.roleRepositoryPort = roleRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void execute(RegisterRequest request) {
        if (userRepositoryPort.existsByUsername(request.username())) {
            throw new BusinessException("Nome de usuário já está em uso.");
        }

        if (userRepositoryPort.existsByEmail(request.email())) {
            throw new BusinessException("E-mail já cadastrado.");
        }

        RoleEntity defaultRole = roleRepositoryPort.findByName("ROLE_OPERATOR")
                .orElseThrow(() -> new IllegalStateException("Perfil padrão de operador não encontrado no sistema."));

        UserEntity user = new UserEntity(null, request.username(), request.email(), passwordEncoder.encode(request.password()), true, null, Set.of(defaultRole));

        userRepositoryPort.save(user);
    }
}
