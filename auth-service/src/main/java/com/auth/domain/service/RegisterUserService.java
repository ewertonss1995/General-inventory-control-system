package com.auth.domain.service;

import com.auth.domain.exception.BusinessException;
import com.auth.ports.in.RegisterUserUseCase;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.ports.out.PasswordEncoderPort;
import com.auth.domain.model.User;
import com.auth.domain.model.Role;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterUserService implements RegisterUserUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);

    private final RoleRepositoryPort roleRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final MeterRegistry meterRegistry;

    public RegisterUserService(RoleRepositoryPort roleRepositoryPort, 
                                UserRepositoryPort userRepositoryPort, 
                                PasswordEncoderPort passwordEncoderPort,
                                MeterRegistry meterRegistry) {
        this.roleRepositoryPort = roleRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void execute(User user) {
        Timer.builder("usecase.auth.register.time")
                .description("Tempo total de execução do caso de uso de registro de usuário")
                .tag("layer", "usecase")
                .register(meterRegistry)
                .record(() -> {
                    log.info("Iniciando processo de registro de usuário: " + user.getUsername());
                    
                    if (userRepositoryPort.existsByUsername(user.getUsername())) {
                        log.error("Tentativa de registro com nome de usuário já existente: " + user.getUsername());
                        recordFailure("username_already_exists");
                        throw new BusinessException("Nome de usuário já está em uso.");
                    }

                    if (userRepositoryPort.existsByEmail(user.getEmail())) {
                        log.error("Tentativa de registro com e-mail já cadastrado: " + user.getEmail());
                        recordFailure("email_already_exists");
                        throw new BusinessException("E-mail já cadastrado.");
                    }

                    Role defaultRole = roleRepositoryPort.findByName("ROLE_OPERATOR")
                            .orElseThrow(() -> {
                                log.error("Perfil padrão 'ROLE_OPERATOR' não encontrado no banco de dados.");
                                recordFailure("default_role_not_found");
                                return new IllegalStateException("Perfil padrão de operador não encontrado no sistema.");
                            });

                    user.setPassword(passwordEncoderPort.encode(user.getPassword()));
                    user.setRoles(Set.of(defaultRole));

                    userRepositoryPort.save(user);

                    meterRegistry.counter("business.auth.register.success", "layer", "usecase").increment();
                    log.info("Usuário registrado com sucesso: " + user.getUsername());
                });
    }

    private void recordFailure(String reason) {
        meterRegistry.counter("business.auth.register.failures",
                "layer", "usecase",
                "reason", reason).increment();
    }
}
