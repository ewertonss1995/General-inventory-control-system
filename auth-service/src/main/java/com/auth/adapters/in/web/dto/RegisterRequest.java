package com.auth.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Objeto de requisição para cadastro de novo usuário")
public record RegisterRequest(
    @Schema(description = "Nome de usuário único para acesso ao sistema", example = "eweerton_dev", minLength = 4, maxLength = 50)
    @NotBlank(message = "O nome de usuário é obrigatório")
    @Size(min = 4, max = 50, message = "O nome de usuário deve ter entre 4 e 50 caracteres")
    String username,

    @Schema(description = "Endereço de e-mail válido e único", example = "eweerton@exemplo.com")
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    String email,

    @Schema(description = "Senha de acesso do usuário (mínimo de 6 caracteres)", example = "SenhaSegura123!", minLength = 6)
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String password
) {}