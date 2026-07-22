package com.inventory.control.web.system.adapters.in.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

import java.math.BigDecimal;

public record ProductRequest(
    String sku,
    String name,
    String description,
    BigDecimal price,
    Integer quantity,
    Long categoryId
) {}