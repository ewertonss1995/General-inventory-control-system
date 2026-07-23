package com.inventory.control.system.adapters.in.web.dto;

import com.inventory.control.system.domain.model.enums.StockMovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateStockRequest(
    @NotNull(message = "A quantidade é obrigatória") 
    @Min(value = 1, message = "A quantidade deve ser de no mínimo 1 item") 
    Integer quantity,

    @NotNull(message = "O tipo de movimentação (IN/OUT) é obrigatório") 
    StockMovementType movementType
) {}