package com.auth.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de resposta com os dados do token de autenticação")
public record TokenResponse(
    @Schema(description = "JWT Access Token para ser enviado no cabeçalho Authorization das demais requisições", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,

    @Schema(description = "Tipo do token emitido", example = "Bearer")
    String tokenType,

    @Schema(description = "Tempo de expiração do token em segundos", example = "7200")
    long expiresInSeconds
) {}