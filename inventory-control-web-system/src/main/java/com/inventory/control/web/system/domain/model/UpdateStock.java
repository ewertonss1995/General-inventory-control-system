package com.inventory.control.web.system.domain.model;

public record UpdateStock(
    String sku,
    Integer previousQuantity, 
    Integer newQuantity,
    String movementType, 
    String message) 
{}
