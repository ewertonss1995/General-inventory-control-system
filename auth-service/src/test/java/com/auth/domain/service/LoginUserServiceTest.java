package com.auth.domain.service;

import com.auth.domain.exception.BusinessException;
import com.auth.domain.model.Login;
import com.auth.domain.model.User;
import com.auth.mocks.AuthMockFactory;
import com.auth.ports.in.TokenUseCase;
import com.auth.ports.out.PasswordEncoderPort;
import com.auth.ports.out.UserRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TokenUseCase tokenUseCase;

    private MeterRegistry meterRegistry;
    private LoginUserService loginUserService;

    @BeforeEach
    void setUp() {
        // Usa a implementação em memória para processar contadores e timers de forma real e rápida
        meterRegistry = new SimpleMeterRegistry();
        loginUserService = new LoginUserService(
                userRepositoryPort,
                passwordEncoderPort,
                tokenUseCase,
                meterRegistry
        );
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        Login login = AuthMockFactory.createLogin();
        User user = AuthMockFactory.createUser("usuario_teste", "usuario@email.com", "encoded_password", true);
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock.token";

        when(userRepositoryPort.findByUsernameOrEmail(login.getUsernameOrEmail(), login.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(login.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(tokenUseCase.generateToken(user))
                .thenReturn(expectedToken);

        String token = loginUserService.execute(login);

        assertNotNull(token);
        assertEquals(expectedToken, token);

        verify(userRepositoryPort).findByUsernameOrEmail(login.getUsernameOrEmail(), login.getUsernameOrEmail());
        verify(passwordEncoderPort).matches(login.getPassword(), user.getPassword());
        verify(tokenUseCase).generateToken(user);

        assertEquals(1.0, meterRegistry.counter("business.auth.login.success", "layer", "usecase").count());
    }

    @Test
    void shouldThrowBusinessExceptionWhenUserNotFound() {
        Login login = AuthMockFactory.createLogin();

        when(userRepositoryPort.findByUsernameOrEmail(login.getUsernameOrEmail(), login.getUsernameOrEmail()))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> loginUserService.execute(login));

        assertEquals("Credenciais inválidas.", exception.getMessage());

        verify(userRepositoryPort).findByUsernameOrEmail(login.getUsernameOrEmail(), login.getUsernameOrEmail());
        verify(passwordEncoderPort, never()).matches(anyString(), anyString());
        verify(tokenUseCase, never()).generateToken(any());

        assertEquals(1.0, meterRegistry.counter("business.auth.login.failures", "layer", "usecase", "reason", "user_not_found").count());
    }

    @Test
    void shouldThrowBusinessExceptionWhenAccountIsInactive() {
        Login login = AuthMockFactory.createLogin();
        User inactiveUser = AuthMockFactory.createUser("usuario_teste", "usuario@email.com", "encoded_password", false);

        when(userRepositoryPort.findByUsernameOrEmail(login.getUsernameOrEmail(), login.getUsernameOrEmail()))
                .thenReturn(Optional.of(inactiveUser));

        BusinessException exception = assertThrows(BusinessException.class, () -> loginUserService.execute(login));

        assertEquals("Conta de usuário inativa.", exception.getMessage());

        verify(userRepositoryPort).findByUsernameOrEmail(login.getUsernameOrEmail(), login.getUsernameOrEmail());
        verify(passwordEncoderPort, never()).matches(anyString(), anyString());
        verify(tokenUseCase, never()).generateToken(any());

        assertEquals(1.0, meterRegistry.counter("business.auth.login.failures", "layer", "usecase", "reason", "account_inactive").count());
    }

    @Test
    void shouldThrowBusinessExceptionWhenPasswordIsInvalid() {
        Login login = AuthMockFactory.createLogin();
        User user = AuthMockFactory.createUser("usuario_teste", "usuario@email.com", "encoded_password", true);

        when(userRepositoryPort.findByUsernameOrEmail(login.getUsernameOrEmail(), login.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(login.getPassword(), user.getPassword()))
                .thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> loginUserService.execute(login));

        assertEquals("Credenciais inválidas.", exception.getMessage());

        verify(userRepositoryPort).findByUsernameOrEmail(login.getUsernameOrEmail(), login.getUsernameOrEmail());
        verify(passwordEncoderPort).matches(login.getPassword(), user.getPassword());
        verify(tokenUseCase, never()).generateToken(any());

        assertEquals(1.0, meterRegistry.counter("business.auth.login.failures", "layer", "usecase", "reason", "invalid_password").count());
    }
}