package com.inventory.control.system.adapters.in.web.dto.response;

public record UpdateStockResponse(
    String sku,
    Integer quantity, 
    String movementType, 
    String message) 
{}
