package com.inventory.control.web.system.adapters.out.auth;

import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.adapters.in.web.mapper.AuthenticateMapper;
import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.domain.model.TokenUser;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static com.inventory.control.web.system.adapters.out.auth.AuthUserMockFactory.DEFAULT_TOKEN;

import static com.inventory.control.web.system.adapters.out.auth.AuthUserMockFactory.DEFAULT_TOKEN_TYPE;
import static com.inventory.control.web.system.adapters.out.auth.AuthUserMockFactory.createLoginUser;
import static com.inventory.control.web.system.adapters.out.auth.AuthUserMockFactory.createRegisterUser;
import static com.inventory.control.web.system.adapters.out.auth.AuthUserMockFactory.createTokenResponse;
import static com.inventory.control.web.system.adapters.out.auth.AuthUserMockFactory.createTokenUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUserAdapterTest {

    @Mock
    private AuthenticateMapper mapper;

    @Mock
    private AuthFeignClient authFeignClient;

    private MeterRegistry meterRegistry;

    private AuthUserAdapter authUserAdapter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        authUserAdapter = new AuthUserAdapter(mapper, authFeignClient, meterRegistry);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        RegisterUser registerUser = createRegisterUser();

        assertDoesNotThrow(() -> authUserAdapter.createUser(registerUser));

        verify(authFeignClient).register(registerUser);

        assertEquals(1, meterRegistry.find("client.feign.auth.time")
                .tag("operation", "createUser")
                .tag("target", "auth_service")
                .timer().count());
    }

    @Test
    void shouldRecordFailureMetricWhenCreateUserFails() {
        RegisterUser registerUser = createRegisterUser();
        doThrow(new RuntimeException("Error registering user")).when(authFeignClient).register(registerUser);

        assertThrows(RuntimeException.class, () -> authUserAdapter.createUser(registerUser));

        verify(authFeignClient).register(registerUser);

        assertEquals(1.0, meterRegistry.find("client.feign.auth.failures")
                .tag("operation", "createUser")
                .tag("error_type", "RuntimeException")
                .counter().count());
    }

    @Test
    void shouldUserLoginSuccessfully() {
        LoginUser loginUser = createLoginUser();
        TokenResponse tokenResponse = createTokenResponse();
        TokenUser expectedTokenUser = createTokenUser();

        when(authFeignClient.login(loginUser)).thenReturn(ResponseEntity.ok(tokenResponse));
        when(mapper.toTokenUser(tokenResponse)).thenReturn(expectedTokenUser);

        TokenUser result = authUserAdapter.userLogin(loginUser);

        assertNotNull(result);
        assertEquals(DEFAULT_TOKEN, result.getAccessToken());
        assertEquals(DEFAULT_TOKEN_TYPE, result.getTokenType());

        verify(authFeignClient).login(loginUser);
        verify(mapper).toTokenUser(tokenResponse);

        assertEquals(1, meterRegistry.find("client.feign.auth.time")
                .tag("operation", "userLogin")
                .tag("target", "auth_service")
                .timer().count());
    }

    @Test
    void shouldRecordFailureMetricWhenUserLoginFails() {
        LoginUser loginUser = createLoginUser();
        when(authFeignClient.login(loginUser)).thenThrow(new RuntimeException("Unauthorized"));

        assertThrows(RuntimeException.class, () -> authUserAdapter.userLogin(loginUser));

        verify(authFeignClient).login(loginUser);

        assertEquals(1.0, meterRegistry.find("client.feign.auth.failures")
                .tag("operation", "userLogin")
                .tag("error_type", "RuntimeException")
                .counter().count());
    }
}