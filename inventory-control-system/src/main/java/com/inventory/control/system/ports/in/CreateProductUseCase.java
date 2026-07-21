package com.inventory.control.system.ports.in;

import com.inventory.control.system.domain.model.Product;
import java.math.BigDecimal;

public interface CreateProductUseCase {
    Product execute(String sku, String name, String description, BigDecimal price, Integer quantity, Long categoryId);
}