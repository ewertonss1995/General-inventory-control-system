package com.auth.domain.service;

import com.auth.adapters.out.database.entity.UserEntity;
import com.auth.ports.in.TokenUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenService implements TokenUseCase {

    private final JwtEncoder jwtEncoder;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generateToken(UserEntity user) {
        log.debug("Iniciando geração de token JWT para o usuário ID: {}", user.getId());

        Instant now = Instant.now();
        long expiresInSeconds = 7200L;

        String scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
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
            
            log.info("Token JWT gerado com sucesso [Subject/UserId: {}, Scopes: {}, ExpiraEm: {}s]", 
                    user.getId(), scope, expiresInSeconds);
                    
            return tokenValue;
        } catch (Exception ex) {
            log.error("Erro ao codificar/assinar o token JWT para o usuário ID: {}", user.getId(), ex);
            throw ex;
        }
    }
}
