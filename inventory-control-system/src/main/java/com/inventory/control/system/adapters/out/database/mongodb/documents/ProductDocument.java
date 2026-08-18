package com.inventory.control.system.adapters.out.database.mongodb.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String sku;
    
    private String name;
    private String description;
    private BigDecimal price;
    private CategoryInfo category;
    private Map<String, Object> attributes;
    private Instant createdAt;

    public ProductDocument() {}

    public ProductDocument(String id, String sku, String name, String description, BigDecimal price, CategoryInfo category) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public CategoryInfo getCategory() { return category; }
    public void setCategory(CategoryInfo category) { this.category = category; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    public Instant getCreatedAt() { return createdAt; }
}
