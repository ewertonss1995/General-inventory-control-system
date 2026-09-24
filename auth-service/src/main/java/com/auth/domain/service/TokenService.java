package com.auth.domain.service;

import com.auth.ports.in.TokenUseCase;
import com.auth.ports.out.JwtTokenProviderPort;
import com.auth.domain.model.User;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenService implements TokenUseCase {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    private final JwtTokenProviderPort tokenProviderPort;
    private final MeterRegistry meterRegistry;

    public TokenService(JwtTokenProviderPort tokenProviderPort, MeterRegistry meterRegistry) {
        this.tokenProviderPort = tokenProviderPort;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public String generateToken(User user) {
        return Timer.builder("usecase.auth.token.generate.time")
                .description("Tempo de execução da geração do token JWT no domínio")
                .tag("layer", "usecase")
                .register(meterRegistry)
                .record(() -> {
                    log.debug("Iniciando geração de token para o usuário ID: {}", user.getId());

                    try {
                        String token = tokenProviderPort.generateToken(user);
                        
                        meterRegistry.counter("business.auth.token.generate.success", "layer", "usecase").increment();
                        log.info("Token gerado com sucesso no domínio para o usuário ID: {}", user.getId());
                        
                        return token;
                    } catch (Exception ex) {
                        log.error("Falha ao gerar token JWT para o usuário ID: {}", user.getId(), ex);
                        meterRegistry.counter("business.auth.token.generate.failures",
                                "layer", "usecase",
                                "exception", ex.getClass().getSimpleName()).increment();
                        throw ex;
                    }
                });
    }
}
