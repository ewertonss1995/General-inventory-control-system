package com.auth.adapters.out;

import com.auth.domain.model.Role;
import com.auth.adapters.out.database.repository.RoleRepository;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.adapters.out.exception.DatabaseException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(RolePersistenceAdapter.class);

    private final RoleRepository roleRepository;
    private final MeterRegistry meterRegistry;

    public RolePersistenceAdapter(RoleRepository roleRepository, MeterRegistry meterRegistry) {
        this.roleRepository = roleRepository;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return Timer.builder("db.auth.role.time")
                .description("Tempo de busca de perfis (roles) por nome")
                .tag("layer", "adapter-out")
                .tag("operation", "findByName")
                .register(meterRegistry)
                .record(() -> {
                    log.debug("Buscando perfil (role) por nome no banco de dados: {}", name);
                    try {
                        return roleRepository.findByName(name)
                                .map(roleEntity -> new Role(roleEntity.getId(), roleEntity.getName()));
                    } catch (DataAccessException ex) {
                        log.error("Erro ao buscar perfil (role) no banco de dados [RoleName: {}]", name, ex);
                        meterRegistry.counter("db.auth.role.failures",
                                "layer", "adapter-out",
                                "operation", "findByName",
                                "exception", ex.getClass().getSimpleName()).increment();
                        throw new DatabaseException("Erro ao buscar perfil (role) do usuário: ", ex);
                    }
                });
    }
}
