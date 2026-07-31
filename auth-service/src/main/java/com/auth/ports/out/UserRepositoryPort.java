package com.auth.ports.out;

import com.auth.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void save(User user);
}