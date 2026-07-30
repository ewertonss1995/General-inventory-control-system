package com.auth.adapters.out.database;

import com.auth.adapters.out.database.entity.RoleEntity;
import com.auth.adapters.out.database.repository.RoleRepository;
import com.auth.ports.out.RoleRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import com.auth.adapters.in.web.exception.DatabaseException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private final RoleRepository roleRepository;

    public RolePersistenceAdapter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<RoleEntity> findByName(String name) {
        log.debug("Buscando perfil (role) por nome no banco de dados: {}", name);
        try {
            return roleRepository.findByName(name);
        } catch (DataAccessException ex) {
            log.error("Erro ao buscar perfil (role) no banco de dados [RoleName: {}]", name, ex);
            throw new DatabaseException("Erro ao buscar perfil (role) do usuário: ", ex);
        }
    }
}
