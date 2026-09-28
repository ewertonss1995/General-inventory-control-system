package com.auth.domain.service;

import com.auth.ports.in.TokenUseCase;
import com.auth.ports.out.JwtTokenProviderPort;
import com.auth.domain.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenService implements TokenUseCase {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    private final JwtTokenProviderPort tokenProviderPort;

    public TokenService(JwtTokenProviderPort tokenProviderPort) {
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public String generateToken(User user) {
        log.debug("Iniciando geração de token para o usuário ID: {}", user.getId());

        String token = tokenProviderPort.generateToken(user);

        log.info("Token gerado com sucesso no domínio para o usuário ID: {}", user.getId());
        return token;
    }
}
