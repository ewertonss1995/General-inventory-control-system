package com.inventory.control.web.system.ports.in.product;

import com.inventory.control.web.system.domain.model.Product;

public interface PostProductUseCase {
    Product execute(Product product);
}