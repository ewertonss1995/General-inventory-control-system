package com.inventory.control.web.system.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta contendo o token de acesso emitido para autenticação")
public record TokenResponse(
    @Schema(description = "Token de acesso no formato JWT Bearer", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,

    @Schema(description = "Tipo do token emitido", example = "Bearer")
    String tokenType,

    @Schema(description = "Tempo total de validade do token em segundos", example = "7200")
    long expiresInSeconds
) {}