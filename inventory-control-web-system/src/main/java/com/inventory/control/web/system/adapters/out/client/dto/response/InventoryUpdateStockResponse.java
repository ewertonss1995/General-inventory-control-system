package com.inventory.control.web.system.adapters.out.client.dto.response;

public record InventoryUpdateStockResponse(
    String sku,
    Integer previousQuantity, 
    Integer newQuantity, 
    String movementType, 
    String message) 
{}
