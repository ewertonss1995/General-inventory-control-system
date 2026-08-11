package com.inventory.control.web.system.adapters.in.web.dto.response;

import java.math.BigDecimal;

public record SaveProductResponse(
    Long id,
    String sku,
    String name,
    String description,
    BigDecimal price,
    Integer quantity,
    String categoryName
) {}