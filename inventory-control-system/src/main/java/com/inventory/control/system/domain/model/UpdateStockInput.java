package com.inventory.control.system.domain.model;

import com.inventory.control.system.domain.model.enums.StockMovementType;

public record UpdateStockInput(
    Integer quantity,
    StockMovementType movementType
) {}