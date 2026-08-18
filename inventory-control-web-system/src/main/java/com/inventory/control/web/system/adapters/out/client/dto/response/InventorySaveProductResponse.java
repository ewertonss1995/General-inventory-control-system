package com.inventory.control.web.system.adapters.out.client.dto.response;

import java.math.BigDecimal;

public record InventorySaveProductResponse(
    String id,
    String sku,
    String name,
    String description,
    BigDecimal price,
    Integer quantity,
    String categoryName
) {}