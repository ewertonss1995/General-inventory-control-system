package com.auth.ports.in;

import com.auth.adapters.out.database.repository.UserRepository;
import com.auth.adapters.out.database.entity.UserEntity;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.adapters.out.database.entity.UserEntity;
import java.util.Optional;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    public UserPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> findByUsernameOrEmail(String username, String email) {
        log.info("Buscando usuário por nome de usuário ou e-mail: " + username + " / " + email);
        return userRepository.findByUsernameOrEmail(username, email);
    }

    @Override
    public boolean existsByUsername(String username) {
        log.info("Verificando existência de usuário por nome de usuário: " + username);
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.info("Verificando existência de usuário por e-mail: " + email);
        return userRepository.existsByEmail(email);
    }

    @Override
    public void save(UserEntity user) {
        log.info("Salvando usuário: " + user.getUsername() + ", E-mail: " + user.getEmail());
        userRepository.save(user);
    }
}
