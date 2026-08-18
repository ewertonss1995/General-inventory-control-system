package com.inventory.control.web.system.adapters.out.client.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryProductRequest(
    @NotBlank(message = "SKU é obrigatório") String sku,
    @NotBlank(message = "Nome é obrigatório") String name,
    String description,
    @NotNull(message = "Preço é obrigatório") @DecimalMin("0.0") BigDecimal price,
    @NotNull(message = "Quantidade é obrigatória") @Min(0) Integer quantity,
    @NotNull(message = "Id da categoria é obrigatório") String categoryId
) {}