package com.auth.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto de requisição para autenticação de usuário")
public record LoginRequest(
    @Schema(description = "Nome de usuário ou endereço de e-mail cadastrado", example = "eweerton_dev")
    @NotBlank(message = "O usuário ou e-mail é obrigatório")
    String usernameOrEmail,

    @Schema(description = "Senha do usuário", example = "SenhaSegura123!")
    @NotBlank(message = "A senha é obrigatória")
    String password
) {}