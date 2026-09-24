package com.auth.infrastructure.security;

import com.auth.ports.out.JwtTokenProviderPort;

import com.auth.domain.model.Role;
import com.auth.domain.model.User;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Component
public class JwtTokenProvider implements JwtTokenProviderPort {

    @Value("${jwt.private-key-path}")
    private Resource privateKeyResource;

    @Value("${jwt.public-key-path}")
    private Resource publicKeyResource;

    @Value("${jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    private JwtEncoder jwtEncoder;
    private RSAPublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        RSAPrivateKey privateKey = loadPrivateKey(privateKeyResource);
        this.publicKey = loadPublicKey(publicKeyResource);

        RSAKey rsaKey = new RSAKey.Builder(this.publicKey)
                .privateKey(privateKey)
                .build();

        var jwkSource = new com.nimbusds.jose.jwk.source.ImmutableJWKSet<>(
                new com.nimbusds.jose.jwk.JWKSet(rsaKey)
        );

        this.jwtEncoder = new NimbusJwtEncoder(jwkSource);
    }

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(jwtExpirationMs);
        String subject = user.getUsername() != null ? user.getUsername() : user.getId().toString();

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getUsername())
                .claim("roles", roles)
                .claim("email", user.getEmail())
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    private RSAPrivateKey loadPrivateKey(Resource resource) throws Exception {
        try (InputStream is = resource.getInputStream()) {
            String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decode = Base64.getDecoder().decode(key);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(decode));
        }
    }

    private RSAPublicKey loadPublicKey(Resource resource) throws Exception {
        try (InputStream is = resource.getInputStream()) {
            String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decode = Base64.getDecoder().decode(key);
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decode));
        }
    }
}
