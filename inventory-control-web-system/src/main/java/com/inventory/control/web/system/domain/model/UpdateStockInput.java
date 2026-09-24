package com.inventory.control.web.system.domain.model;

public class UpdateStockInput {
    private Integer quantity;
    private String movementType;

    public UpdateStockInput() {};

    public UpdateStockInput(Integer quantity, String movementType) {
        this.quantity = quantity;
        this.movementType = movementType;
    };

    public Integer getQuantity() { return quantity; }
    public String getMovementType() { return movementType; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
}