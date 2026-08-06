package com.inventory.control.system.ports.in.product;

import com.inventory.control.system.domain.model.Product;

public interface CreateProductUseCase {
    Product execute(Product product);
}