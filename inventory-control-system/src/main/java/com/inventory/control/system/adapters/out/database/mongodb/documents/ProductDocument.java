package com.inventory.control.system.adapters.out.database.mongodb.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder.Default
    private Instant createdAt = Instant.now();
}
