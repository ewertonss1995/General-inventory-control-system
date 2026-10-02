package com.auth.domain.service;

import com.auth.domain.model.User;
import com.auth.mocks.AuthMockFactory;
import com.auth.ports.out.JwtTokenProviderPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtTokenProviderPort tokenProviderPort;

    private MeterRegistry meterRegistry;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        tokenService = new TokenService(tokenProviderPort, meterRegistry);
    }

    @Test
    void shouldGenerateTokenSuccessfully() {
        User user = AuthMockFactory.createUser();
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token.mock";

        when(tokenProviderPort.generateToken(user)).thenReturn(expectedToken);

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertEquals(expectedToken, token);

        verify(tokenProviderPort).generateToken(user);

        assertEquals(1.0, meterRegistry.counter("business.auth.token.generate.success", "layer", "usecase").count());
    }

    @Test
    void shouldIncrementFailureCounterAndRethrowExceptionWhenTokenGenerationFails() {
        User user = AuthMockFactory.createUser();
        RuntimeException exception = new RuntimeException("Erro ao assinar token");

        when(tokenProviderPort.generateToken(user)).thenThrow(exception);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> tokenService.generateToken(user));

        assertEquals("Erro ao assinar token", thrown.getMessage());

        verify(tokenProviderPort).generateToken(user);

        assertEquals(1.0, meterRegistry.counter("business.auth.token.generate.failures",
                "layer", "usecase",
                "exception", "RuntimeException").count());
    }
}