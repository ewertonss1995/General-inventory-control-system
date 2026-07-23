package com.auth.ports.out;

import com.auth.adapters.out.database.entity.UserEntity;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void save(UserEntity user);
}