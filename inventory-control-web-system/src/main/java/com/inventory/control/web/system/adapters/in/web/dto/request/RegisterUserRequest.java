package com.inventory.control.web.system.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Objeto de requisição para cadastro de usuário no ecossistema Web")
public record RegisterUserRequest(
        @Schema(description = "Nome de usuário único no sistema", example = "maria_silva", minLength = 4, maxLength = 50)
        @NotBlank(message = "Username é obrigatório") 
        @Size(min = 4, max = 50) 
        String username,

        @Schema(description = "Endereço de e-mail corporativo ou pessoal", example = "maria.silva@empresa.com")
        @NotBlank(message = "E-mail é obrigatório") 
        @Email 
        String email,

        @Schema(description = "Senha de acesso do usuário (mínimo 6 caracteres)", example = "SenhaRobusta@2026", minLength = 6)
        @NotBlank(message = "A senha é obrigatória") 
        @Size(min = 6) 
        String password
) {}