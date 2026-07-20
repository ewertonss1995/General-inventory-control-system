package com.inventory.control.system.domain.model;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Category category;

    public Product(Long id, String sku, String name, String description, BigDecimal price, Integer quantity, Category category) {
        this.id = id;
        this.sku = sku != null ? sku.toUpperCase() : null;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public void addStock(Integer amount) {
        if (amount == null || amount <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        this.quantity += amount;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public Category getCategory() { return category; }
}