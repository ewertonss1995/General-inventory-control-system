package com.inventory.control.system.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
    @NotNull(message = "Nome da categoria é obrigatório") String name,
    String description
) {}