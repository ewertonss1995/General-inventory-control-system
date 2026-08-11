package com.inventory.control.web.system.adapters.in.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "Username é obrigatório") @Size(min = 4, max = 50) String username,
        @NotBlank(message = "E-mail é obrigatório") @Email String email,
        @NotBlank(message = "A senha é obrigatória") @Size(min = 6) String password) {
}
