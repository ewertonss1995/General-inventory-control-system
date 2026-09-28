package com.inventory.control.web.system.domain.model;

public class UpdateStock{
    private String sku;
    private Integer previousQuantity; 
    private Integer newQuantity;
    private String movementType; 
    private String message;

    public UpdateStock() {}
    
    public UpdateStock(
        String sku, 
        Integer previousQuantity, 
        Integer newQuantity, 
        String movementType, 
        String message) {
            this.sku = sku;
            this.previousQuantity = previousQuantity;
            this.newQuantity = newQuantity;
            this.movementType = movementType;
            this.message = message;

    }

    public String getSku() { return sku; }
    public Integer getPreviousQuantity() { return previousQuantity; }
    public Integer getNewQuantity() { return newQuantity; }
    public String getMovementType() { return movementType; }
    public String getMessage() { return message; }
    public void setSku(String sku) { this.sku = sku != null ? sku.toUpperCase() : null; }
    public void setPreviousQuantity(Integer previousQuantity) { this.previousQuantity = previousQuantity; }
    public void setNewQuantity(Integer newQuantity) { this.newQuantity = newQuantity; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public void setMessage(String message) { this.message = message; }
}
