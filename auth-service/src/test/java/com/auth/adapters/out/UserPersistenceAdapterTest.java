package com.auth.adapters.out;

import com.auth.adapters.out.database.entity.RoleEntity;
import com.auth.adapters.out.database.entity.UserEntity;
import com.auth.adapters.out.database.repository.UserRepository;
import com.auth.adapters.out.exception.DatabaseException;
import com.auth.domain.model.User;
import com.auth.mocks.AuthMockFactory;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

    @Mock
    private UserRepository userRepository;

    private MeterRegistry meterRegistry;
    private UserPersistenceAdapter userPersistenceAdapter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        userPersistenceAdapter = new UserPersistenceAdapter(userRepository, meterRegistry);
    }

    @Test
    void shouldReturnUserWhenFindByUsernameOrEmailSucceeds() {
        String identifier = "usuario_teste";
        UUID userId = UUID.randomUUID();
        RoleEntity roleEntity = new RoleEntity(UUID.randomUUID(), "ROLE_OPERATOR");
        UserEntity userEntity = new UserEntity(userId, identifier, "usuario@email.com", "encoded_password", Set.of(roleEntity));
        userEntity.setActive(true);

        when(userRepository.findByUsernameOrEmail(identifier, identifier)).thenReturn(Optional.of(userEntity));

        Optional<User> result = userPersistenceAdapter.findByUsernameOrEmail(identifier, identifier);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals(identifier, result.get().getUsername());
        assertEquals("usuario@email.com", result.get().getEmail());
        assertTrue(result.get().isActive());
        assertEquals(1, result.get().getRoles().size());

        verify(userRepository).findByUsernameOrEmail(identifier, identifier);
    }

    @Test
    void shouldReturnEmptyOptionalWhenFindByUsernameOrEmailNotFound() {
        String identifier = "usuario_inexistente";

        when(userRepository.findByUsernameOrEmail(identifier, identifier)).thenReturn(Optional.empty());

        Optional<User> result = userPersistenceAdapter.findByUsernameOrEmail(identifier, identifier);

        assertTrue(result.isEmpty());

        verify(userRepository).findByUsernameOrEmail(identifier, identifier);
    }

    @Test
    void shouldThrowDatabaseExceptionAndIncrementCounterWhenFindByUsernameOrEmailFails() {
        String identifier = "usuario_teste";
        QueryTimeoutException exception = new QueryTimeoutException("Database connection timeout");

        when(userRepository.findByUsernameOrEmail(identifier, identifier)).thenThrow(exception);

        DatabaseException thrown = assertThrows(DatabaseException.class,
                () -> userPersistenceAdapter.findByUsernameOrEmail(identifier, identifier));

        assertNotNull(thrown.getCause());
        assertEquals(exception, thrown.getCause());

        verify(userRepository).findByUsernameOrEmail(identifier, identifier);

        assertEquals(1.0, meterRegistry.counter("db.auth.user.failures",
                "layer", "adapter-out",
                "operation", "findByUsernameOrEmail",
                "exception", "QueryTimeoutException").count());
    }

    @Test
    void shouldReturnTrueWhenExistsByUsernameReturnsTrue() {
        String username = "usuario_teste";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        boolean exists = userPersistenceAdapter.existsByUsername(username);

        assertTrue(exists);

        verify(userRepository).existsByUsername(username);
    }

    @Test
    void shouldReturnFalseWhenExistsByUsernameReturnsFalse() {
        String username = "usuario_novo";

        when(userRepository.existsByUsername(username)).thenReturn(false);

        boolean exists = userPersistenceAdapter.existsByUsername(username);

        assertFalse(exists);

        verify(userRepository).existsByUsername(username);
    }

    @Test
    void shouldThrowDatabaseExceptionAndIncrementCounterWhenExistsByUsernameFails() {
        String username = "usuario_teste";
        QueryTimeoutException exception = new QueryTimeoutException("Timeout");

        when(userRepository.existsByUsername(username)).thenThrow(exception);

        DatabaseException thrown = assertThrows(DatabaseException.class,
                () -> userPersistenceAdapter.existsByUsername(username));

        assertEquals(exception, thrown.getCause());

        verify(userRepository).existsByUsername(username);

        assertEquals(1.0, meterRegistry.counter("db.auth.user.failures",
                "layer", "adapter-out",
                "operation", "existsByUsername",
                "exception", "QueryTimeoutException").count());
    }

    @Test
    void shouldReturnTrueWhenExistsByEmailReturnsTrue() {
        String email = "usuario@email.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean exists = userPersistenceAdapter.existsByEmail(email);

        assertTrue(exists);

        verify(userRepository).existsByEmail(email);
    }

    @Test
    void shouldReturnFalseWhenExistsByEmailReturnsFalse() {
        String email = "novo@email.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean exists = userPersistenceAdapter.existsByEmail(email);

        assertFalse(exists);

        verify(userRepository).existsByEmail(email);
    }

    @Test
    void shouldThrowDatabaseExceptionAndIncrementCounterWhenExistsByEmailFails() {
        String email = "usuario@email.com";
        QueryTimeoutException exception = new QueryTimeoutException("Timeout");

        when(userRepository.existsByEmail(email)).thenThrow(exception);

        DatabaseException thrown = assertThrows(DatabaseException.class,
                () -> userPersistenceAdapter.existsByEmail(email));

        assertEquals(exception, thrown.getCause());

        verify(userRepository).existsByEmail(email);

        assertEquals(1.0, meterRegistry.counter("db.auth.user.failures",
                "layer", "adapter-out",
                "operation", "existsByEmail",
                "exception", "QueryTimeoutException").count());
    }

    @Test
    void shouldSaveUserSuccessfully() {
        User user = AuthMockFactory.createUser();
        UserEntity savedEntity = new UserEntity(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), Set.of());

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        userPersistenceAdapter.save(user);

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void shouldThrowDatabaseExceptionAndIncrementCounterWhenSaveFails() {
        User user = AuthMockFactory.createUser();
        QueryTimeoutException exception = new QueryTimeoutException("Database error");

        when(userRepository.save(any(UserEntity.class))).thenThrow(exception);

        DatabaseException thrown = assertThrows(DatabaseException.class,
                () -> userPersistenceAdapter.save(user));

        assertEquals(exception, thrown.getCause());

        verify(userRepository).save(any(UserEntity.class));

        assertEquals(1.0, meterRegistry.counter("db.auth.user.failures",
                "layer", "adapter-out",
                "operation", "save",
                "exception", "QueryTimeoutException").count());
    }
}