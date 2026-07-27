package com.inventory.control.web.system.adapters.out.client.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

import java.math.BigDecimal;

public record InventoryProductRequest(
    String sku,
    String name,
    String description,
    BigDecimal price,
    Integer quantity,
    Long categoryId
) {}