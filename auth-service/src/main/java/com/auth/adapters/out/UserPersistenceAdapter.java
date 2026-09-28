package com.auth.adapters.out;

import com.auth.adapters.out.database.entity.UserEntity;
import com.auth.adapters.out.database.entity.RoleEntity;
import com.auth.adapters.out.database.repository.UserRepository;
import com.auth.domain.model.User;
import com.auth.domain.model.Role;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.adapters.out.exception.DatabaseException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(UserPersistenceAdapter.class);

    private final UserRepository userRepository;
    private final MeterRegistry meterRegistry;

    public UserPersistenceAdapter(UserRepository userRepository, MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return executeWithTimer("findByUsernameOrEmail", () -> {
            log.debug("Buscando usuário por username: {} ou email: {}", username, email);
            try {
                return userRepository.findByUsernameOrEmail(username, email)
                    .map(userEntity -> {
                        Set<Role> roles = userEntity.getRoles().stream()
                                .map(role -> new Role(role.getId(), role.getName()))
                                .collect(Collectors.toSet());

                        return new User(
                                userEntity.getId(),
                                userEntity.getUsername(),
                                userEntity.getEmail(),
                                userEntity.isActive(),
                                userEntity.getPassword(),
                                roles
                        );
                    });
            } catch (DataAccessException ex) {
                log.error("Erro ao buscar usuário no banco de dados [username: {}, email: {}]", username, email, ex);
                recordDatabaseFailure("findByUsernameOrEmail", ex);
                throw new DatabaseException("Usuário não encontrado: ", ex);
            }
        });
    }

    @Override
    public boolean existsByUsername(String username) {
        return executeWithTimer("existsByUsername", () -> {
            log.debug("Verificando existência do username: {}", username);
            try {
                return userRepository.existsByUsername(username);
            } catch (DataAccessException ex) {
                log.error("Erro ao verificar existência do username no banco de dados: {}", username, ex);
                recordDatabaseFailure("existsByUsername", ex);
                throw new DatabaseException("Erro durante verificação de existencia de usuário: ", ex);
            }
        });
    }

    @Override
    public boolean existsByEmail(String email) {
        return executeWithTimer("existsByEmail", () -> {
            log.debug("Verificando existência do e-mail: {}", email);
            try {
                return userRepository.existsByEmail(email);
            } catch (DataAccessException ex) {
                log.error("Erro ao verificar existência do e-mail no banco de dados: {}", email, ex);
                recordDatabaseFailure("existsByEmail", ex);
                throw new DatabaseException("Erro durante verificação de existencia de email: ", ex);
            }
        });
    }

    @Override
    public void save(User user) {
        executeWithTimer("save", () -> {
            log.debug("Persistindo usuário no banco de dados: username={}, email={}", user.getUsername(), user.getEmail());
            try {
                Set<RoleEntity> roleEntities = user.getRoles().stream()
                        .map(role -> new RoleEntity(role.getId(), role.getName()))
                        .collect(Collectors.toSet());
                            
                UserEntity userEntity = new UserEntity(
                    user.getId(),
                    user.getUsername(), 
                    user.getEmail(), 
                    user.getPassword(), 
                    roleEntities
                );

                UserEntity savedEntity = userRepository.save(userEntity);
                log.info("Usuário persistido com sucesso [ID: {}, username: {}]", savedEntity.getId(), savedEntity.getUsername());
                return null;
            } catch (DataAccessException ex) {
                log.error("Erro ao salvar usuário no banco de dados [username: {}, email: {}]", user.getUsername(), user.getEmail(), ex);
                recordDatabaseFailure("save", ex);
                throw new DatabaseException("Erro ao salvar usuário no banco de dados.", ex);
            }
        });
    }

    private <T> T executeWithTimer(String operation, Supplier<T> supplier) {
        return Timer.builder("db.auth.user.time")
                .description("Tempo de execução das operações de persistência de usuário")
                .tag("layer", "adapter-out")
                .tag("operation", operation)
                .register(meterRegistry)
                .record(supplier);
    }

    private void executeWithTimer(String operation, Runnable runnable) {
        Timer.builder("db.auth.user.time")
                .description("Tempo de execução das operações de persistência de usuário")
                .tag("layer", "adapter-out")
                .tag("operation", operation)
                .register(meterRegistry)
                .record(runnable);
    }

    private void recordDatabaseFailure(String operation, Exception ex) {
        meterRegistry.counter("db.auth.user.failures",
                "layer", "adapter-out",
                "operation", operation,
                "exception", ex.getClass().getSimpleName()).increment();
    }
}
