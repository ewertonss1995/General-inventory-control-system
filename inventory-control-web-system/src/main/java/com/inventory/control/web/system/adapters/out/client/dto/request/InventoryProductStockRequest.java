package com.inventory.control.web.system.adapters.out.client.dto.request;

import com.inventory.control.web.system.adapters.out.client.dto.enums.StockMovementType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryProductStockRequest(
    @NotNull(message = "A quantidade é obrigatória") 
    @Min(value = 1, message = "A quantidade deve ser de no mínimo 1 item") 
    Integer quantity,

    @NotNull(message = "O tipo de movimentação (IN/OUT) é obrigatório") 
    StockMovementType movementType
) {}