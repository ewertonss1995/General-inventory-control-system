package com.inventory.control.web.system.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto de requisição com credenciais de login")
public record LoginRequest(
        @Schema(description = "Username ou endereço de e-mail do usuário", example = "maria_silva")
        @NotBlank(message = "O usuário ou e-mail é obrigatório") 
        String usernameOrEmail,

        @Schema(description = "Senha de acesso do usuário", example = "SenhaRobusta@2026")
        @NotBlank(message = "A senha é obrigatória") 
        String password
) {}