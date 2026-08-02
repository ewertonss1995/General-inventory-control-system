package com.inventory.control.system.ports.in;

import com.inventory.control.system.domain.model.Product;

public interface UpdateProductUseCase {
    Product execute(Product product);
}