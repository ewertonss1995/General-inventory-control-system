package com.inventory.control.web.system.domain.model;

import java.math.BigDecimal;

public class ProductMessage {

    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String categoryId;
    private String categoryName;
    private String categoryDescription;

    public ProductMessage() {
    }

    public ProductMessage(String sku, 
                          String name, 
                          String description, 
                          BigDecimal price, 
                          Integer quantity, 
                          String categoryId, 
                          String categoryName, 
                          String categoryDescription) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }
}