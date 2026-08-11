package com.inventory.control.web.system.domain.model;

public record UpdateStock(
    String sku,
    Integer quantity, 
    String movementType, 
    String message) 
{}
