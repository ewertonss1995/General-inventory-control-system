package com.auth.domain.service;

import com.auth.adapters.out.database.entity.RoleEntity;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.adapters.out.database.repository.RoleRepository;

import java.util.Optional;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private final RoleRepository roleRepository;

    public RolePersistenceAdapter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<RoleEntity> findByName(String name) {    
        return roleRepository.findByName(name);
    }

}
