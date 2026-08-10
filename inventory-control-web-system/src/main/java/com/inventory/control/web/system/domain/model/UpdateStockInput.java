package com.inventory.control.web.system.domain.model;

import com.inventory.control.web.system.domain.model.enums.StockMovementType;

public record UpdateStockInput(
    String sku,
    Integer quantity,
    StockMovementType movementType
) {}