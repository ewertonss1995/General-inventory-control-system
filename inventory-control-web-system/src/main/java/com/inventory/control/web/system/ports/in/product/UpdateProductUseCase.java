package com.inventory.control.web.system.ports.in.product;

import com.inventory.control.web.system.domain.model.Product;

public interface UpdateProductUseCase {
    Product execute(Product product);
}