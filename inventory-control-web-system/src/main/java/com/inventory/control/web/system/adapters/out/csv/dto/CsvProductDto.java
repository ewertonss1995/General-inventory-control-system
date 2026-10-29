package com.inventory.control.web.system.adapters.out.csv.dto;

public record CsvProductDto(
        String sku,
        String name,
        String description,
        String price,
        String quantity,
        String categoryId,
        String categoryName,
        String categoryDescription
) {}