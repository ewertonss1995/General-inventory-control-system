package com.auth.adapters.out.database;

import com.auth.adapters.out.database.entity.RoleEntity;
import com.auth.domain.model.Role;
import com.auth.adapters.out.database.repository.RoleRepository;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.adapters.out.exception.DatabaseException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(RolePersistenceAdapter.class);

    private final RoleRepository roleRepository;

    public RolePersistenceAdapter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        log.debug("Buscando perfil (role) por nome no banco de dados: {}", name);
        try {
            return roleRepository.findByName(name)
            .map(roleEntity -> new Role(roleEntity.getId(), roleEntity.getName()));
        } catch (DataAccessException ex) {
            log.error("Erro ao buscar perfil (role) no banco de dados [RoleName: {}]", name, ex);
            throw new DatabaseException("Erro ao buscar perfil (role) do usuário: ", ex);
        }
    }
}
