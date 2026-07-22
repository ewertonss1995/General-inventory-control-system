package com.inventory.control.web.system.domain.model;

import java.math.BigDecimal;


public class ProductItem {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String categoryName;

    public ProductItem(Long id, String sku, String name, String description, BigDecimal price, Integer quantity, String categoryName) {
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
}