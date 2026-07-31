package com.auth.adapters.out;

import com.auth.adapters.out.database.entity.UserEntity;
import com.auth.adapters.out.database.entity.RoleEntity;
import com.auth.adapters.out.database.repository.UserRepository;
import com.auth.domain.model.User;
import com.auth.domain.model.Role;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.adapters.in.web.exception.DatabaseException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(UserPersistenceAdapter.class);

    private final UserRepository userRepository;

    public UserPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        log.debug("Buscando usuário por username: {} ou email: {}", username, email);
        try {
            Optional<UserEntity> userEntity = userRepository.findByUsernameOrEmail(username, email);

            Set<Role> roles = userEntity.get().getRoles().stream()
            .map(role ->  new Role(role.getId(), role.getName()))
            .collect(Collectors.toSet());
            
            User user = new User(
                userEntity.get().getId(), 
                userEntity.get().getEmail(), 
                userEntity.get().isActive(), 
                userEntity.get().getPassword(), 
                roles
            );
            
            return Optional.of(user);
        } catch (DataAccessException ex) {
            log.error("Erro ao buscar usuário no banco de dados [username: {}, email: {}]", username, email, ex);
            throw new DatabaseException("Usuário não encontrado: ", ex);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("Verificando existência do username: {}", username);
        try {
            return userRepository.existsByUsername(username);
        } catch (DataAccessException ex) {
            log.error("Erro ao verificar existência do username no banco de dados: {}", username, ex);
            throw new DatabaseException("Erro durante verificação de existencia de usuário: ", ex);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Verificando existência do e-mail: {}", email);
        try {
            return userRepository.existsByEmail(email);
        } catch (DataAccessException ex) {
            log.error("Erro ao verificar existência do e-mail no banco de dados: {}", email, ex);
            throw new DatabaseException("Erro durante verificação de existencia de email: ", ex);
        }
    }

    @Override
    public void save(User user) {
        log.debug("Persistindo usuário no banco de dados: username={}, email={}", user.getUsername(), user.getEmail());
        try {
            
            Set<RoleEntity> roles = user.getRoles().stream()
            .map(role ->  new RoleEntity(role.getId(), role.getName()))
            .collect(Collectors.toSet());
                        
            UserEntity userEntity = new UserEntity(
                user.getUsername(), 
                user.getEmail(), 
                user.getPassword(), 
                roles);

            userRepository.save(userEntity);
            log.info("Usuário persistido com sucesso [ID: {}, username: {}]", userEntity.getId(), userEntity.getUsername());
        } catch (DataAccessException ex) {
            log.error("Erro ao salvar usuário no banco de dados [username: {}]", user.getUsername(), ex);
            throw new DatabaseException("Erro durante registro usuário: ", ex);
        }
    }
}
