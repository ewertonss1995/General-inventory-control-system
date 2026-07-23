package com.auth.domain.service;

import com.auth.adapters.out.database.entity.RoleEntity;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.adapters.out.database.repository.RoleRepository;

import java.util.Optional;

public class RegisterUserAdapter implements RoleRepositoryPort {

    private final RoleRepository roleRepository;

    public RegisterUserAdapter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<RoleEntity> findByName(String name) {
        return roleRepository.findByName(name);
    }

}