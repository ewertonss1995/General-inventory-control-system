package com.auth.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "O usuário ou e-mail é obrigatório")
    String usernameOrEmail,

    @NotBlank(message = "A senha é obrigatória")
    String password
) {}