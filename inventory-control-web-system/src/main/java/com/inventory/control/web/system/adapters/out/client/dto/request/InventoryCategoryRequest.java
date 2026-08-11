package com.inventory.control.web.system.adapters.out.client.dto.request;

import jakarta.validation.constraints.NotNull;

public record InventoryCategoryRequest(
    @NotNull(message = "Nome da categoria é obrigatório") String name,
    String description
) {}