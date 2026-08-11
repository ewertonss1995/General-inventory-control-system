package com.inventory.control.web.system.adapters.out.client.dto.response;

public record InventoryUpdateStockResponse(
    Integer quantity, 
    String movementType, 
    String message) 
{}
