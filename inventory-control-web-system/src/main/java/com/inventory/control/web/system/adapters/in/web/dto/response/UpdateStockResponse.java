package com.inventory.control.web.system.adapters.in.web.dto.response;

public record UpdateStockResponse(
    String sku,
    Integer previousQuantity,
    Integer newQuantity, 
    String movementType, 
    String message) 
{}
