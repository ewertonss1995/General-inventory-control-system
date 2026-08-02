package com.inventory.control.system.domain.model;

import java.math.BigDecimal;
import com.inventory.control.system.domain.exception.BusinessException;

public class Product {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Long categoryId;
    private String categoryName;


    public Product(Long id, String sku, String name, String description, BigDecimal price, Integer quantity, Long categoryId, String categoryName) {
        this.id = id;
        this.sku = sku != null ? sku.toUpperCase() : null;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity != null ? quantity : 0;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Product(String sku, String name, String description, BigDecimal price, Integer quantity, Long categoryId) {
        this.sku = sku != null ? sku.toUpperCase() : null;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity != null ? quantity : 0;
        this.categoryId = categoryId;
    }

    public Product(String sku, String name, String description, BigDecimal price, Integer quantity, Long categoryId, String categoryName) {
        this.sku = sku != null ? sku.toUpperCase() : null;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity != null ? quantity : 0;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }


    public void addStock(Integer quantity) {
        if (quantity == null || quantity <= 0) throw new IllegalArgumentException("A quantidade para entrada de estoque deve ser maior que zero.");
        this.quantity += quantity;
    }

    public void removeStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("A quantidade para baixa de estoque deve ser maior que zero.");
        }
        if (this.quantity < quantity) {
            throw new BusinessException(String.format(
                "Saldo insuficiente para o produto SKU '%s'. Saldo atual: %d, Quantidade solicitada: %d",
                this.sku, this.quantity, quantity
            ));
        }
        this.quantity -= quantity;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
}
