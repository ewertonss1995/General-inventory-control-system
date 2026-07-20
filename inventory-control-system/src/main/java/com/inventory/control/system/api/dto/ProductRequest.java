package com.inventory.control.system.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank(message = "SKU é obrigatório") String sku,
    @NotBlank(message = "Nome é obrigatório") String name,
    String description,
    @NotNull(message = "Preço é obrigatório") @DecimalMin("0.0") BigDecimal price,
    @NotNull(message = "Quantidade é obrigatória") @Min(0) Integer quantity,
    @NotNull(message = "ID da categoria é obrigatório") Long categoryId
) {}