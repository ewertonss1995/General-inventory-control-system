package com.auth.ports.in;

import com.auth.adapters.out.database.repository.UserRepository;
import com.auth.adapters.out.database.entity.UserEntity;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.adapters.out.database.entity.UserEntity;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class AuthenticateUserAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    public AuthenticateUserAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void save(UserEntity user) {
        userRepository.save(user);
    }
}
