package com.auth.infrastructure.persistence.jpa.repository;

import com.auth.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}