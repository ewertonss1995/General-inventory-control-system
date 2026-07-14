package com.auth.infrastructure.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
@RequestMapping("/.well-known")
@Tag(name = "Certificates", description = "Endpoints de infraestrutura e criptografia de chaves públicas")
public class JwkSetController {

    private final RSAPublicKey rsaPublicKey;

    public JwkSetController(RSAPublicKey rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    @GetMapping("/jwks.json")
    @Operation(summary = "Fornece a chave pública para verificação offline do JWT")
    public Map<String, Object> keys() {
        RSAKey key = new RSAKey.Builder(this.rsaPublicKey).build();
        return new JWKSet(key).toJSONObject();
    }
}