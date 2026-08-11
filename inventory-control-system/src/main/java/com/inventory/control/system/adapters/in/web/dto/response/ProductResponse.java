package com.inventory.control.system.adapters.in.web.dto.response;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String sku,
    String name,
    String description,
    BigDecimal price,
    Integer quantity,
    CategoryResponse category
) {}