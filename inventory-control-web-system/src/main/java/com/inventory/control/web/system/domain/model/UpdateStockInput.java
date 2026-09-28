package com.inventory.control.web.system.domain.model;

public record UpdateStockInput(
    Integer quantity,
    String movementType
) {}