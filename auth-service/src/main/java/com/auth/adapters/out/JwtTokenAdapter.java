package com.auth.adapters.out;

import com.auth.ports.out.TokenProviderPort;
import com.auth.domain.model.User;
import com.auth.domain.model.Role;
import com.auth.adapters.out.exception.TokenGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.Collections;
import java.util.Objects;
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
        log.debug("Gerando token JWT para o usuário com ID: {}", user.getId());

        try {
            Instant now = Instant.now();

            String scope = Objects.requireNonNullElse(user.getRoles(), Collections.<Role>emptySet())
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(" "));

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("auth-service")
                    .subject(user.getId().toString())
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(7200L))
                    .claim("email", user.getEmail())
                    .claim("scope", scope)
                    .build();

            String tokenValue = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            
            log.info("Token JWT assinado com sucesso [UserId: {}, Scopes: {}]", user.getId(), scope);
            return tokenValue;

        } catch (JwtEncodingException ex) {
            log.error("Erro de codificação ao gerar o token JWT para o usuário [UserId: {}]", user.getId(), ex);
            throw new TokenGenerationException("Falha ao assinar e gerar o token de acesso.", ex);
        } catch (Exception ex) {
            log.error("Erro inesperado ao criar o token JWT para o usuário [UserId: {}]", user.getId(), ex);
            throw new TokenGenerationException("Erro interno no serviço de autenticação.", ex);
        }
    }
}
