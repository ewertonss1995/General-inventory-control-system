package com.application.usecase;

import com.application.dto.RegisterRequest;
import com.domain.exception.BusinessException;
import com.infrastructure.persistence.jpa.entity.RoleEntity;
import com.infrastructure.persistence.jpa.entity.UserEntity;
import com.infrastructure.persistence.jpa.repository.RoleRepository;
import com.infrastructure.persistence.jpa.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository, 
                               RoleRepository roleRepository, 
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Nome de usuário já está em uso.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("E-mail já cadastrado.");
        }

        // Busca a role default cadastrada via Flyway no passo 22
        RoleEntity defaultRole = roleRepository.findByName("ROLE_OPERATOR")
                .orElseThrow(() -> new IllegalStateException("Perfil padrão de operador não encontrado no sistema."));

        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // Criptografia com BCrypt
                .roles(Set.of(defaultRole))
                .build();

        userRepository.save(user);
    }
}