package com.inventory.control.web.system.domain.model;

import java.math.BigDecimal;

public record Product(
    String sku,
    String name,
    String description,
    BigDecimal price,
    Integer quantity,
    Long categoryId
) {}