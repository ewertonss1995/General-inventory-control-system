package com.auth.adapters.in.web;

import com.auth.ports.in.JwkSetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/.well-known")
@Tag(name = "Certificates", description = "Endpoints de infraestrutura e criptografia de chaves públicas")
public class JwkSetController {

    private final JwkSetUseCase jwkSetUseCase;

    public JwkSetController(JwkSetUseCase jwkSetUseCase) {
        this.jwkSetUseCase = jwkSetUseCase;
    }

    @GetMapping("/jwks.json")
    @Operation(summary = "Fornece a chave pública para verificação offline do JWT")
    public Map<String, Object> getKeys() {
        return jwkSetUseCase.getKeys();
    }
}
