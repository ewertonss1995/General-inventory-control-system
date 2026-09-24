package com.inventory.control.web.system.adapters.out.auth;

import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.adapters.in.web.mapper.AuthenticateMapper;
import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.domain.model.TokenUser;
import com.inventory.control.web.system.ports.out.AuthenticateFeignPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class AuthUserAdapter implements AuthenticateFeignPort {

    private static final Logger log = LoggerFactory.getLogger(AuthUserAdapter.class);

    private final AuthenticateMapper mapper;
    private final AuthFeignClient authFeignClient;
    private final MeterRegistry meterRegistry;

    public AuthUserAdapter(AuthenticateMapper mapper, 
                           AuthFeignClient authFeignClient, 
                           MeterRegistry meterRegistry) {
        this.mapper = mapper;
        this.authFeignClient = authFeignClient;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void createUser(RegisterUser registerUser) {
        executeVoidWithTimer("createUser", () -> {
            try {
                log.debug("Iniciando processo de registro de usuário: {} no serviço de autenticação",
                        registerUser.getUsername());
                authFeignClient.register(registerUser);
            } catch (Exception ex) {
                recordFailure("createUser", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }

    @Override
    public TokenUser userLogin(LoginUser loginUser) {
        return executeWithTimer("userLogin", () -> {
            try {
                log.debug("Iniciando processo de login de usuário: {} no serviço de autenticação",
                        loginUser.getUsernameOrEmail());
                ResponseEntity<TokenResponse> responseToken = authFeignClient.login(loginUser);
                return mapper.toTokenUser(responseToken.getBody());
            } catch (Exception ex) {
                recordFailure("userLogin", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }

    private <T> T executeWithTimer(String operation, Supplier<T> supplier) {
        return Timer.builder("client.feign.auth.time")
                .description("Tempo de execução das chamadas HTTP via Feign no AuthUserAdapter")
                .tag("layer", "adapter_out")
                .tag("target", "auth_service")
                .tag("operation", operation)
                .register(meterRegistry)
                .record(supplier);
    }

    private void executeVoidWithTimer(String operation, Runnable runnable) {
        Timer.builder("client.feign.auth.time")
                .description("Tempo de execução das chamadas HTTP via Feign no AuthUserAdapter")
                .tag("layer", "adapter_out")
                .tag("target", "auth_service")
                .tag("operation", operation)
                .register(meterRegistry)
                .record(runnable);
    }

    private void recordFailure(String operation, String errorType) {
        meterRegistry.counter("client.feign.auth.failures",
                "layer", "adapter_out",
                "target", "auth_service",
                "operation", operation,
                "error_type", errorType).increment();
    }
}
