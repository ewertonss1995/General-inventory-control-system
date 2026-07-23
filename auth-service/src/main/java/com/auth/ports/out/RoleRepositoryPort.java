package com.auth.ports.out;

import com.auth.adapters.out.database.entity.RoleEntity;
import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<RoleEntity> findByName(String name);
}