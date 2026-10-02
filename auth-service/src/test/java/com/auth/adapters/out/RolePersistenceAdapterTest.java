package com.auth.adapters.out;

import com.auth.adapters.out.database.entity.RoleEntity;
import com.auth.adapters.out.database.repository.RoleRepository;
import com.auth.adapters.out.exception.DatabaseException;
import com.auth.domain.model.Role;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolePersistenceAdapterTest {

    @Mock
    private RoleRepository roleRepository;

    private MeterRegistry meterRegistry;
    private RolePersistenceAdapter rolePersistenceAdapter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        rolePersistenceAdapter = new RolePersistenceAdapter(roleRepository, meterRegistry);
    }

    @Test
    void shouldReturnRoleWhenFoundByName() {
        String roleName = "ROLE_OPERATOR";
        UUID roleId = UUID.randomUUID();
        RoleEntity roleEntity = new RoleEntity(roleId, roleName);

        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));

        Optional<Role> result = rolePersistenceAdapter.findByName(roleName);

        assertTrue(result.isPresent());
        assertEquals(roleId, result.get().getId());
        assertEquals(roleName, result.get().getName());

        verify(roleRepository).findByName(roleName);
    }

    @Test
    void shouldReturnEmptyOptionalWhenRoleNotFound() {
        String roleName = "ROLE_NOT_EXISTENT";

        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        Optional<Role> result = rolePersistenceAdapter.findByName(roleName);

        assertTrue(result.isEmpty());

        verify(roleRepository).findByName(roleName);
    }

    @Test
    void shouldThrowDatabaseExceptionAndIncrementCounterWhenDataAccessExceptionOccurs() {
        String roleName = "ROLE_OPERATOR";
        QueryTimeoutException exception = new QueryTimeoutException("Database connection timeout");

        when(roleRepository.findByName(roleName)).thenThrow(exception);

        DatabaseException thrown = assertThrows(DatabaseException.class, () -> rolePersistenceAdapter.findByName(roleName));

        assertNotNull(thrown.getCause());
        assertEquals(exception, thrown.getCause());

        verify(roleRepository).findByName(roleName);

        assertEquals(1.0, meterRegistry.counter("db.auth.role.failures",
                "layer", "adapter-out",
                "operation", "findByName",
                "exception", "QueryTimeoutException").count());
    }
}