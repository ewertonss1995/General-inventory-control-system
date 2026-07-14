package com.auth.domain.service;

import com.auth.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(UserEntity user) {
        Instant now = Instant.now();
        long expiresInSeconds = 7200L; // 2 horas de expiração do Token

        // Coleta os papéis de acesso do usuário (Ex: ROLE_ADMIN)
        String scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // Define as Claims profissionais exigidas no Confluence
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresInSeconds))
                .claim("email", user.getEmail())
                .claim("scope", scope) // Claim 'scope' ou 'roles' mapeia as permissões no Spring Security
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}