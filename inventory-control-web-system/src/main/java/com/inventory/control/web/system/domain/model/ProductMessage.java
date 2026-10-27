package com.inventory.control.web.system.domain.model;

import java.math.BigDecimal;

public record ProductMessage(
        String sku,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        String categoryName,
        String categoryDescription
) {}
