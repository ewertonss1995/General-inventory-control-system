package com.inventory.control.system.domain.model;

import java.math.BigDecimal;
import com.inventory.control.system.domain.model.Category;

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
        if (amount == null || amount <= 0) throw new IllegalArgumentException("A quantidade para entrada de estoque deve ser maior que zero.");
        this.quantity += amount;
    }

    public void removeStock(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("A quantidade para baixa de estoque deve ser maior que zero.");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException(String.format(
                "Saldo insuficiente para o produto SKU '%s'. Saldo atual: %d, Quantidade solicitada: %d",
                this.sku, this.quantity, amount
            ));
        }
        this.quantity -= amount;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public Category getCategory() { return category; }
}
