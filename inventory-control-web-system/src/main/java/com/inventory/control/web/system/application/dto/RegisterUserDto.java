package com.inventory.control.web.system.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserDto(
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 4, max = 50)
    String username,

    @NotBlank(message = "E-mail é obrigatório")
    @Email
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6)
    String password
) {}