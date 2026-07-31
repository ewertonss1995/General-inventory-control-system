package com.auth.adapters.out.security;

import com.auth.ports.out.TokenProviderPort;
import com.auth.domain.model.User;
import com.auth.domain.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenAdapter implements TokenProviderPort {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenAdapter.class);

    private final JwtEncoder jwtEncoder;

    public JwtTokenAdapter(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        long expiresInSeconds = 7200L;

        String scope = user.getRoles().stream()
        .map(Role::getName)
        .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresInSeconds))
                .claim("email", user.getEmail())
                .claim("scope", scope)
                .build();

        try {
            String tokenValue = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            log.info("Token JWT assinado com sucesso no adapter [UserId: {}, Scopes: {}]", user.getId(), scope);
            return tokenValue;
        } catch (Exception ex) {
            log.error("Erro ao assinar o token JWT no JwtTokenAdapter para o usuário ID: {}", user.getId(), ex);
            throw ex;
        }
    }
}
