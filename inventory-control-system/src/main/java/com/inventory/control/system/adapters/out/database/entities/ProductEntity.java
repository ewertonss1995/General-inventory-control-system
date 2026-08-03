package com.inventory.control.system.adapters.out.database.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_products")
public class ProductEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 50)
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    public ProductEntity() {}
    
    public ProductEntity(
        Long id, 
        String sku, 
        String name, 
        String description, 
        BigDecimal price, 
        Integer quantity, 
        CategoryEntity category) {
            this.id = id;
            this.sku = sku;
            this.name = name;
            this.description = description;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
    }

    public ProductEntity(
        String sku, 
        String name, 
        String description, 
        BigDecimal price, 
        Integer quantity, 
        CategoryEntity category) {
            this.sku = sku;
            this.name = name;
            this.description = description;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public CategoryEntity getCategory() { return category; }
    public void setCategory(CategoryEntity category) { this.category = category; }
}