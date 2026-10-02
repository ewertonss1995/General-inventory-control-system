package com.auth.domain.service;

import com.auth.domain.exception.BusinessException;
import com.auth.domain.model.Role;
import com.auth.domain.model.User;
import com.auth.mocks.AuthMockFactory;
import com.auth.ports.out.PasswordEncoderPort;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.ports.out.UserRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private MeterRegistry meterRegistry;
    private RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        registerUserService = new RegisterUserService(
                roleRepositoryPort,
                userRepositoryPort,
                passwordEncoderPort,
                meterRegistry
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        User user = AuthMockFactory.createUser();
        Role defaultRole = new Role(UUID.randomUUID(), "ROLE_OPERATOR");
        String encodedPassword = "encoded_senha123";

        when(userRepositoryPort.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(user.getEmail())).thenReturn(false);
        when(roleRepositoryPort.findByName("ROLE_OPERATOR")).thenReturn(Optional.of(defaultRole));
        when(passwordEncoderPort.encode("senha123")).thenReturn(encodedPassword);

        registerUserService.execute(user);

        assertEquals(encodedPassword, user.getPassword());
        assertNotNull(user.getRoles());
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(defaultRole));

        verify(userRepositoryPort).existsByUsername(user.getUsername());
        verify(userRepositoryPort).existsByEmail(user.getEmail());
        verify(roleRepositoryPort).findByName("ROLE_OPERATOR");
        verify(passwordEncoderPort).encode("senha123");
        verify(userRepositoryPort).save(user);

        assertEquals(1.0, meterRegistry.counter("business.auth.register.success", "layer", "usecase").count());
    }

    @Test
    void shouldThrowBusinessExceptionWhenUsernameAlreadyExists() {
        User user = AuthMockFactory.createUser();

        when(userRepositoryPort.existsByUsername(user.getUsername())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> registerUserService.execute(user));

        assertEquals("Nome de usuário já está em uso.", exception.getMessage());

        verify(userRepositoryPort).existsByUsername(user.getUsername());
        verify(userRepositoryPort, never()).existsByEmail(anyString());
        verify(roleRepositoryPort, never()).findByName(anyString());
        verify(passwordEncoderPort, never()).encode(anyString());
        verify(userRepositoryPort, never()).save(any());

        assertEquals(1.0, meterRegistry.counter("business.auth.register.failures", "layer", "usecase", "reason", "username_already_exists").count());
    }

    @Test
    void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
        User user = AuthMockFactory.createUser();

        when(userRepositoryPort.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(user.getEmail())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> registerUserService.execute(user));

        assertEquals("E-mail já cadastrado.", exception.getMessage());

        verify(userRepositoryPort).existsByUsername(user.getUsername());
        verify(userRepositoryPort).existsByEmail(user.getEmail());
        verify(roleRepositoryPort, never()).findByName(anyString());
        verify(passwordEncoderPort, never()).encode(anyString());
        verify(userRepositoryPort, never()).save(any());

        assertEquals(1.0, meterRegistry.counter("business.auth.register.failures", "layer", "usecase", "reason", "email_already_exists").count());
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenDefaultRoleNotFound() {
        User user = AuthMockFactory.createUser();

        when(userRepositoryPort.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(user.getEmail())).thenReturn(false);
        when(roleRepositoryPort.findByName("ROLE_OPERATOR")).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> registerUserService.execute(user));

        assertEquals("Perfil padrão de operador não encontrado no sistema.", exception.getMessage());

        verify(userRepositoryPort).existsByUsername(user.getUsername());
        verify(userRepositoryPort).existsByEmail(user.getEmail());
        verify(roleRepositoryPort).findByName("ROLE_OPERATOR");
        verify(passwordEncoderPort, never()).encode(anyString());
        verify(userRepositoryPort, never()).save(any());

        assertEquals(1.0, meterRegistry.counter("business.auth.register.failures", "layer", "usecase", "reason", "default_role_not_found").count());
    }
}