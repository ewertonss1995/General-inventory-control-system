package com.auth.domain.service;

import java.security.interfaces.RSAPublicKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import com.auth.ports.in.JwkSetUseCase;

import java.util.Map;

public class JwkSetService implements JwkSetUseCase {
    
    private final RSAPublicKey rsaPublicKey;

    public JwkSetService(RSAPublicKey rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }
    
    @Override
    public Map<String, Object> getKeys() {
        RSAKey key = new RSAKey.Builder(this.rsaPublicKey).build();
        return new JWKSet(key).toJSONObject();
    }
}
