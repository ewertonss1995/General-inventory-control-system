package com.inventory.control.system.adapters.out.database.postgres.entities;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_stock_balance")
public class StockBalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false, unique = true, length = 64)
    private String productId;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false)
    private Integer quantity;

    @Version
    private Long version;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public StockBalanceEntity() {}

    public StockBalanceEntity(String productId, String sku, Integer quantity) {
        this.productId = productId;
        this.sku = sku;
        this.quantity = quantity;
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getProductId() { return productId; }
    public String getSku() { return sku; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Long getVersion() { return version; }
    public Instant getUpdatedAt() { return updatedAt; }
}
