package com.study.inventory.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "tb_products",
    indexes = {
        @Index(name = "idx_product_sku", columnList = "sku", unique = true)
    }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Deprecated
    protected Product() {}

    public Product(String sku, String name, String description, BigDecimal price, Integer quantity, Category category) {
        this.sku = sku.toUpperCase();
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public void addStock(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("A quantidade de entrada deve ser maior que zero.");
        }
        this.quantity += amount;
    }

    public void removeStock(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("A quantidade de saída deve ser maior que zero.");
        }
        if (this.quantity < amount) {
            throw new IllegalStateException("Saldo de estoque insuficiente para realizar a baixa.");
        }
        this.quantity -= amount;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku.toUpperCase(); }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
