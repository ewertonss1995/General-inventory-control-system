package com.inventory.control.web.system.domain.model;

import java.math.BigDecimal;

public class Product {
    private String id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Category category;

    public Product(){}

    public Product(
        String id, 
        String sku, 
        String name, 
        String description, 
        BigDecimal price, 
        Integer quantity, 
        Category category) {
            this.id = id;
            this.sku = sku != null ? sku.toUpperCase() : null;
            this.name = name;
            this.description = description;
            this.price = price;
            this.quantity = quantity != null ? quantity : 0;
            this.category = category;
    }

    public Product(
        String sku, 
        String name, 
        String description, 
        BigDecimal price, 
        Integer quantity, 
        Category category) {
            this.sku = sku != null ? sku.toUpperCase() : null;
            this.name = name;
            this.description = description;
            this.price = price;
            this.quantity = quantity != null ? quantity : 0;
            this.category = category;
    }
     
    public String getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public Category getCategory() { return category; }
    public void setId(String id) { this.id = id; }
    public void setSku(String sku) { this.sku = sku != null ? sku.toUpperCase() : null; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setCategory(Category category) { this.category = category; }
}