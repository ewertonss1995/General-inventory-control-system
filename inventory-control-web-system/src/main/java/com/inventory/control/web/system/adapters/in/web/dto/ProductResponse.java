package com.inventory.control.web.system.adapters.in.web.dto;

import java.math.BigDecimal;

public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String categoryName;

    public ProductResponse(Long id, String sku, String name, String description, BigDecimal price, Integer quantity, String categoryName) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }   

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}