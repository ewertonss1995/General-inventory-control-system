package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.domain.model.ProductItem;
import com.inventory.control.web.system.domain.model.Product;

public interface CreateProductPort {
    ProductItem createProduct(Product product);
}